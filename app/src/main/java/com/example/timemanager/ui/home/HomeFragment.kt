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
import com.example.timemanager.db.dao.RecordDao
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.TaskDao
import com.google.android.material.card.MaterialCardView
import com.example.timemanager.db.model.Task
import com.example.timemanager.db.model.TaskState
import com.example.timemanager.di.RepositoryModule
import com.example.timemanager.repository.UserPreferencesRepository
import com.example.timemanager.ui.ItemTouchHelperCallback
import com.example.timemanager.ui.home.adapter.TasksAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var taskDao: TaskDao

    @Inject
    lateinit var sheetDao: SheetDao

    @Inject
    lateinit var recordDao: RecordDao

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private val viewModel: HomeViewModel by navGraphViewModels(R.id.home_navigation) {
        HomeViewModelFactory(
            taskRepository = RepositoryModule.provideTaskRepository(taskDao),
            sheetRepository = RepositoryModule.provideSheetRepository(sheetDao),
            recordRepository = RepositoryModule.provideRecordRepository(recordDao),
            userPreferencesRepository = userPreferencesRepository
        )
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val tasksAdapter = TasksAdapter()

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
        initToolBar()
        postponeEnterTransition()
        initTasksRecyclerView()
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
                viewModel.setTaskDone(task.id)
            }

            override fun onTaskDoingClick(task: Task) {
                viewModel.setTaskDoing(task.id)
            }

            override fun onTaskTimingClick(task: Task) {
                findNavController().navigate(R.id.action_navigation_todo_to_navigation_timing)
                viewModel.setTimingTaskId(task.id)
                viewModel.setTimingTaskName(task.name)
            }
        }
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
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
                    tasksAdapter.submitList(viewModel.sortByInfo(it))
                    setProgressValue(getTasksDoneProgress(it))
                    setProgressText(getProgressText(it))
                }
            }
        )
    }

    private fun showEmptyListIllustration() {
        binding.emptyList.visibility = View.VISIBLE
    }

    private fun hideEmptyListIllustration() {
        binding.emptyList.visibility = View.GONE
        // 改为均不能上下滑动
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
            "$done/$total"
        } ?: "0/0"

    private fun setSwipeActions() {
        val swipeController = ItemTouchHelperCallback(
            requireContext(),
            object :
                ItemTouchHelperCallback.ItemTouchHelperAdapter {
                override fun onDelete(position: Int) {
                    tasksAdapter.currentList[position]?.id?.let { taskId ->
                        viewModel.deleteTask(taskId)
                    }
                }

                override fun onItemMove(fromPosition: Int, toPosition: Int) {
                    val list : MutableList<Task> = tasksAdapter.currentList.toMutableList()
                    val prev = list[fromPosition]
                    list.add(
                        if(toPosition > fromPosition) toPosition + 1 else toPosition,
                        prev
                    )
                    if(toPosition > fromPosition) list.removeAt(fromPosition) else list.removeAt(fromPosition + 1)
                    viewModel.setSortInfo(list)
                    tasksAdapter.submitList(list)
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