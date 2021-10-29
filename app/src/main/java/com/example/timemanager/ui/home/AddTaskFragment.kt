package com.example.timemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.timemanager.R
import com.example.timemanager.databinding.AddTaskFragmentBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.model.createTask
import com.example.timemanager.repository.mapper.TaskMapper.toDomain
import com.example.timemanager.repository.mapper.TaskMapper.toEntity
import com.example.timemanager.ui.home.utils.Constants

/**
 * A [Fragment] to create task.
 */
class AddTaskFragment : Fragment() {
    private var _binding: AddTaskFragmentBinding? = null
    private val binding get() = _binding!!
    private var jumpFrom: Int? = null
    private var taskId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = AddTaskFragmentBinding.inflate(inflater, container, false)
        jumpFrom = arguments?.getInt(Constants.FROM, 1)
        taskId = arguments?.getInt(Constants.TASK_ID)
        val task = taskId?.let {
            TimeManagerDatabase.getInstance(requireContext()).taskDao()
                .getTask(it).toDomain()
        }
        binding.taskNameEditText.setText(task?.name)
        binding.taskDescriptionEditText.setText(task?.description)
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
                        insertTask()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initToolBar() {
        val toolbar = binding.addTaskToolbar
        when (jumpFrom) {
            Constants.ADD -> toolbar.title = getString(R.string.list_add_item)
            Constants.EDIT -> toolbar.title = getString(R.string.list_edit_item)
            else -> toolbar.title = getString(R.string.list_add_item)
        }
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.navigation_todo)
        }
    }

    private fun insertTask() {
        if (!validateTaskName()) {
            return
        }
        val name = binding.taskNameEditText.text.toString()
        val description = binding.taskDescriptionEditText.text.toString()
        activity?.let {
            createTask(name, 1, description).toEntity()?.let { taskEntity ->
                TimeManagerDatabase.getInstance(it)
                    .taskDao()
                    .insertTask(taskEntity)
            }
        }
        findNavController().navigateUp()
    }

    private fun validateTaskName(): Boolean {
        // binding.taskNameInput.clearError()
        return if (binding.taskNameEditText.text.isNullOrBlank()) {
            binding.taskNameInput.error = getString(R.string.must_be_not_empty)
            false
        } else true
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}