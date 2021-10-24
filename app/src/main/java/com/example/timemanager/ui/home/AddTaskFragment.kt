package com.example.timemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.timemanager.R
import com.example.timemanager.databinding.AddTaskFragmentBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.model.Task
import com.example.timemanager.db.model.TaskState
import com.example.timemanager.repository.mapper.TaskMapper.toEntity

/**
 * A [Fragment] to create task.
 */
class AddTaskFragment : Fragment(){
    private var _binding: AddTaskFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel = activity?.let { ViewModelProvider(it).get(HomeViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = AddTaskFragmentBinding.inflate(inflater, container, false)
        viewModel?.getTaskId()
        // binding.taskNameEditText.setText()
        initToolBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationUI.setupWithNavController(binding.addTaskToolbar, findNavController())
        binding.addTaskToolbar.apply {
            inflateMenu(R.menu.add_task_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.save_task -> {
                        createTask()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initToolBar() {
        val toolbar = binding.addTaskToolbar
        when(viewModel?.getIsEdit()) {
            true -> toolbar.title = "编辑待办"
            else -> toolbar.title = "添加待办"
        }
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.navigation_todo)
        }
    }

    private fun createTask() {
        if (validateTaskName()) {
            insertTask()
        }
    }

    private fun validateTaskName(): Boolean {
//        binding.taskNameInput.clearError()
        return if (binding.taskNameEditText.text.isNullOrBlank()) {
            binding.taskNameInput.error = getString(R.string.must_be_not_empty)
            false
        } else true
    }

    private fun insertTask() {
        val name = binding.taskNameEditText.text.toString()
        val description = binding.taskDescriptionEditText.text.toString()
        // 构造好要添加的任务/清单
        val task = Task(
            name = name,
            description = description,
            state = TaskState.DOING,
            sheetId = 1,
            tag = null,
            startDate = 123,
            endDate = 123,
            rank = "重要紧急"
        )
        activity?.let {
            task.toEntity()?.let { it1 ->
                TimeManagerDatabase.getInstance(it)
                    .taskDao()
                    .insertTask(it1)
            }
        }
        findNavController().navigateUp()
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}