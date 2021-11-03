package com.example.timemanager.ui.home

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.annotation.IntRange
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanager.R
import com.example.timemanager.databinding.FragmentHomeBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.google.android.material.card.MaterialCardView
import com.example.timemanager.db.model.Task
import com.example.timemanager.db.model.TaskState
import com.example.timemanager.repository.mapper.SheetMapper.toDomain
import com.example.timemanager.ui.SwipeController
import com.example.timemanager.ui.home.adapter.TasksAdapter
import com.example.timemanager.ui.home.utils.Constants
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val tasksAdapter = TasksAdapter()
    private var sheetId: Int = Constants.DEFAULT_SHEET_ID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sheetId = arguments?.getInt(Constants.SHEET_ID) ?: Constants.DEFAULT_SHEET_ID
        initToolBar()
        return binding.root
    }

    private fun initToolBar() {
        val toolbar = binding.toolbar
        // toolbar设置
        toolbar.title = "默认清单列表"
        toolbar.overflowIcon = resources.getDrawable(R.drawable.ic_list_setting)
        toolbar.inflateMenu(R.menu.top_home_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.list_add -> {
                    // 点击右上角+号
                    findNavController().navigate(R.id.navigation_add_task)
                    true
                }
                R.id.menu_sheet -> {
                    // 点击右上角 三
                    findNavController().navigate(R.id.action_navigation_todo_to_navigation_menu_sheet, Bundle().apply {
                        putInt(Constants.SHEET_ID, sheetId)
                    })
                    true
                }
                R.id.list_setting -> {
                    // 点击右上角菜单中的属性设置，跳转到清单编辑页面
                    // Navigate to item settings view
                    true
                }
                R.id.list_delete -> {
                    // 点击右上角菜单中的删除
                    // delete list
                    // 默认清单列表不可删除
                    true
                }
                else -> false
            }
        }
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
            override fun onTaskEditClick(taskId: Int, card: MaterialCardView) {
                findNavController().navigate(R.id.navigation_add_task, Bundle().apply {
                    putInt(Constants.FROM, Constants.EDIT)
                    putInt(Constants.TASK_ID, taskId)
                })
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

            override fun onTaskTimingClick(task: Task) {
                findNavController().navigate(R.id.navigation_timing, Bundle().apply {
                    putString(Constants.TASK_NAME, task.name)
                })
            }
        }
    }

    //@ExperimentalCoroutinesApi
    private fun initSheetSelectedObserver() {
        val sheet = TimeManagerDatabase.getInstance(requireContext()).sheetDao()
            .getSheet(sheetId)
            .toDomain()
        val list = sheet?.tasks
        if (list?.isEmpty() == true) {
            showEmptyListIllustration()
        } else {
            hideEmptyListIllustration()
        }
        tasksAdapter.submitList(list)
        list?.let { getTasksDoneProgress(it) }?.let { setProgressValue(it) }

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
        ObjectAnimator.ofInt(
            binding.progressBar,
            "progress",
            binding.progressBar.progress,
            progress
        ).apply {
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
                        TimeManagerDatabase.getInstance(requireContext()).taskDao()
                            .deleteTask(taskId)
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