package com.example.timemanager.ui.home

import android.animation.ObjectAnimator
import android.content.Intent
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanager.AddTodoActivity
import com.example.timemanager.R
import com.example.timemanager.databinding.FragmentHomeBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.TimeManagerDatabase_Impl
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.SheetDao_Impl
import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.db.dao.TaskDao_Impl
import com.example.timemanager.db.model.Sheet
import com.google.android.material.card.MaterialCardView
import com.example.timemanager.db.model.Task
import com.example.timemanager.db.model.TaskState
import com.example.timemanager.repository.SheetRepository
import com.example.timemanager.repository.TaskRepository
import com.example.timemanager.repository.UserPreferencesRepository
import com.example.timemanager.repository.mapper.SheetMapper.toDomain
import com.example.timemanager.ui.SwipeController
import com.example.timemanager.ui.TasksAdapter
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map

class HomeFragment : Fragment(){

    private lateinit var homeViewModel: HomeViewModel

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val tasksAdapter = TasksAdapter()

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = HomeViewModel(
            UserPreferencesRepository(requireContext()),
            SheetRepository(SheetDao_Impl(TimeManagerDatabase_Impl())),
            TaskRepository(TaskDao_Impl(TimeManagerDatabase_Impl()))
        )
            //ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toolbar = binding.toolbar
        // toolbar设置
        toolbar.title = "默认清单列表"
        toolbar.overflowIcon = resources.getDrawable(R.drawable.ic_list_setting)
        toolbar.inflateMenu(R.menu.top_home_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.list_add -> {
                    // Navigate to item settings view
                    val intent = Intent(context, AddTodoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.list_setting -> {
                    // Navigate to item settings view
                    true
                }
                R.id.list_delete -> {
                    // delete list
                    true
                }
                else -> false
            }
        }

        return root
    }

    //@ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        initTasksRecyclerView()
        initSheetSelectedObserver()
        setSwipeActions()
    }

    private fun initTasksRecyclerView() {
        binding.tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tasksAdapter
        }
        binding.tasksRecyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }
        tasksAdapter.taskClickListener = object : TasksAdapter.TaskClickListener {
            override fun onTaskClick(taskId: Int, card: MaterialCardView) {
                val extras = FragmentNavigatorExtras(
                    card to taskId.toString()
                )
//                val action = NavigationTodoDirections.navToTask(
//                    taskId = taskId
//                )
//                findNavController().navigate(action, extras)
            }

            override fun onTaskDoneClick(task: Task) {
                //homeViewModel.setTaskDone(task.id)
                TimeManagerDatabase.getInstance(requireContext()).taskDao().setTaskDone(task.id)
                initSheetSelectedObserver()
            }

            override fun onTaskDoingClick(task: Task) {
                //homeViewModel.setTaskDoing(task.id)
                TimeManagerDatabase.getInstance(requireContext()).taskDao().setTaskDoing(task.id)
                initSheetSelectedObserver()
            }
        }
    }

    //@ExperimentalCoroutinesApi
    private fun initSheetSelectedObserver() {
        val sheet = TimeManagerDatabase.getInstance(requireContext()).sheetDao().getSheetNotFlow(1).toDomain()
        val list = sheet!!.tasks
        if (list.isEmpty()) {
            showEmptyListIllustration()
        } else {
            hideEmptyListIllustration()
        }
        tasksAdapter.submitList(list)
        setProgressValue(getTasksDoneProgress(list))

//        homeViewModel.sheetSelected.observe(
//            viewLifecycleOwner,
//            { sheet ->
//                binding.projectNameTextView.text = sheet?.name?: "-"
//                sheet?.tasks?.let {
//                    if (it.isEmpty()) {
//                        showEmptyListIllustration()
//                    } else {
//                        hideEmptyListIllustration()
//                    }
//                    tasksAdapter.submitList(it)
//                    setProgressValue(getTasksDoneProgress(it))
//                }
//            }
//        )
    }

    private fun showEmptyListIllustration() {
        binding.emptyList.visibility = View.VISIBLE
        //remove toolbar scroll flags => 不能上下滑动
        (binding.toolbar.layoutParams as AppBarLayout.LayoutParams).scrollFlags = 0
    }

    private fun hideEmptyListIllustration() {
        binding.emptyList.visibility = View.GONE
        //set toolbar scroll flags => 可以上下滑动
        (binding.toolbar.layoutParams as AppBarLayout.LayoutParams).scrollFlags =
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
    }

    private fun setProgressValue(progress: Int) {
        ObjectAnimator.ofInt(binding.progressBar, "progress", binding.progressBar.progress, progress).apply {
            duration = 300.toLong()
            interpolator = AccelerateInterpolator()
        }.start()
        binding.progressTextView.text = getPercentage(progress)
    }

    private fun getTasksDoneProgress(list: List<Task?>): Int =
        list.takeUnless { it.isEmpty() }?.let {
            ((it.filter { task -> task?.state == TaskState.DONE }.size / it.size.toDouble()) * 100).toInt()
        } ?: 0

    private fun getPercentage(@IntRange(from = 0, to = 100) progress: Int) =
        progress.takeIf { it in 0..100 }?.let { "$it%" } ?: "-%"

    private fun setSwipeActions() {
        val swipeController = SwipeController(
            requireContext(),
            object :
                SwipeController.SwipeControllerActions {
                override fun onDelete(position: Int) {
                    tasksAdapter.currentList[position]?.id?.let { taskId ->
                        TimeManagerDatabase.getInstance(requireContext()).taskDao().deleteTask(taskId)
                        initSheetSelectedObserver()
                    }
//                    createMaterialDialog(
//                        requireContext(),
//                        style = R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
//                    ) {
//                        icon(R.drawable.ic_warning_24dp)
//                        title(" ")
//                        message(R.string.delete_task_dialog)
//                        positiveButton(getString(R.string.ok)) {
//                            tasksAdapter.currentList[position]?.id?.let { taskId ->
//                                homeViewModel.deleteTask(
//                                    taskId
//                                )
//                            }
//                        }
//                        negativeButton(getString(R.string.cancel))
//                    }.show()
                }
            }
        )
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.tasksRecyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}