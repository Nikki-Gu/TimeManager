package com.example.timemanager.ui.home

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
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

    private val viewModel: HomeViewModel by navGraphViewModels(R.id.home_navigation)

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sheetId = viewModel.getSheetSelectedId()
        updateSheet()
        initToolBar()
        postponeEnterTransition()
        initTasksRecyclerView()
        initSheetSelectedIdObserver()
        initSheetSelectedObserver()
        setSwipeActions()
    }

    private fun initToolBar() {
        binding.toolbar.apply {
            overflowIcon = resources.getDrawable(R.drawable.ic_list_setting)
            inflateMenu(R.menu.top_home_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.list_add -> {
                        findNavController().navigate(R.id.action_navigation_todo_to_navigation_add_task)
                        viewModel.setEdit(false)
                        true
                    }
                    R.id.menu_sheet -> {
                        findNavController().navigate(R.id.action_navigation_todo_to_navigation_menu_sheet)
                        true
                    }
                    R.id.list_setting -> {
                        // 点击右上角菜单中的属性设置，跳转到清单编辑页面
                        // Navigate to item settings view
                        true
                    }
                    else -> false
                }
            }
        }
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
                findNavController().navigate(R.id.action_navigation_todo_to_navigation_add_task)
                viewModel.setEdit(true)
                viewModel.setEditTaskId(taskId)
            }

            override fun onTaskDoneClick(task: Task) {
                TimeManagerDatabase.getInstance(requireContext()).taskDao().setTaskDone(task.id)
                updateSheet()
            }

            override fun onTaskDoingClick(task: Task) {
                TimeManagerDatabase.getInstance(requireContext()).taskDao().setTaskDoing(task.id)
                updateSheet()
            }

            override fun onTaskTimingClick(task: Task) {
                findNavController().navigate(R.id.navigation_timing, Bundle().apply {
                    putString(Constants.TASK_NAME, task.name)
                })
            }
        }
    }

    private fun initSheetSelectedIdObserver() {
        viewModel.sheetSelectedId.observe(
            viewLifecycleOwner,
            { sheetId ->
                val sheet = TimeManagerDatabase.getInstance(requireContext()).sheetDao()
                    .getSheet(sheetId)
                    .toDomain()
                viewModel.setSheetSelected(sheet)
            }
        )
    }

    private fun initSheetSelectedObserver() {
        viewModel.sheetSelected.observe(
            viewLifecycleOwner,
            { sheet ->
                binding.toolbar.title = sheet?.name
                sheet?.tasks?.let {
                    if (it.isEmpty()) {
                        showEmptyListIllustration()
                    } else {
                        hideEmptyListIllustration()
                    }
                    tasksAdapter.submitList(it)
                    setProgressValue(getTasksDoneProgress(it))
                    setProgressText(getProgressText(it))
                }
            }
        )
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
    }

    private fun setProgressText(text: String) {
        binding.progressTextView.text = text
    }

    private fun getTasksDoneProgress(list: List<Task?>): Int =
        list.takeUnless { it.isEmpty() }?.let {
            ((it.filter { task -> task?.state == TaskState.DONE }.size / it.size.toDouble()) * 100).toInt()
        } ?: 0

    private fun getProgressText(list: List<Task?>): String =
        list.takeUnless { it.isEmpty() }?.let {
            val total = it.size
            var done = 0
            it.forEach { task ->
                if (task?.state == TaskState.DONE)
                    done++
            }
            "$done / $total"
        } ?: "0 / 0"

    private fun setSwipeActions() {
        val swipeController = SwipeController(
            requireContext(),
            object :
                SwipeController.SwipeControllerActions {
                override fun onDelete(position: Int) {
                    tasksAdapter.currentList[position]?.id?.let { taskId ->
                        TimeManagerDatabase.getInstance(requireContext()).taskDao()
                            .deleteTask(taskId)
                    }
                    updateSheet()
                }
            }
        )
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.tasksRecyclerView)
    }

    private fun updateSheet() {
        val sheet = TimeManagerDatabase.getInstance(requireContext()).sheetDao()
            .getSheet(sheetId)
            .toDomain()
        viewModel.setSheetSelected(sheet)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}