package com.example.timemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.example.timemanager.R
import com.example.timemanager.databinding.AddTaskFragmentBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.model.createTask
import com.example.timemanager.db.model.createUpdateTask
import com.example.timemanager.extensions.hideSoftKeyboard
import com.example.timemanager.repository.mapper.TaskMapper.toDomain
import com.example.timemanager.repository.mapper.TaskMapper.toEntity

/**
 * A [Fragment] to create task.
 */
class AddTaskFragment : Fragment() {
    private var _binding: AddTaskFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by navGraphViewModels(R.id.home_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = AddTaskFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initToolBar()
        if (viewModel.isEdit()) {
            val task = viewModel.getEditTaskId()?.let {
                TimeManagerDatabase.getInstance(requireContext()).taskDao()
                    .getTask(it).toDomain()
            }
            binding.taskNameEditText.setText(task?.name)
            binding.taskDescriptionEditText.setText(task?.description)
        }
    }

    private fun initToolBar() {
        NavigationUI.setupWithNavController(binding.addTaskToolbar, findNavController())
        binding.addTaskToolbar.apply {
            title = if (viewModel.isEdit()) {
                getString(R.string.list_edit_item)
            } else {
                getString(R.string.list_add_item)
            }
            inflateMenu(R.menu.add_task_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.save_task -> {
                        if (viewModel.isEdit()) {
                            updateTask()
                        } else {
                            insertTask()
                        }
                        true
                    }
                    else -> false
                }
            }
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun insertTask() {
        if (!validateTaskName()) {
            return
        }
        val name = binding.taskNameEditText.text.toString()
        val description = binding.taskDescriptionEditText.text.toString()
        activity?.let {
            it.hideSoftKeyboard()
            viewModel.getSheetSelectedId().let { sheetId ->
                createTask(name, sheetId, description).toEntity()?.let { taskEntity ->
                    TimeManagerDatabase.getInstance(it)
                        .taskDao()
                        .insertTask(taskEntity)
                }
            }
        }
        findNavController().navigateUp()
    }

    private fun updateTask() {
        if (!validateTaskName()) {
            return
        }
        val name = binding.taskNameEditText.text.toString()
        val description = binding.taskDescriptionEditText.text.toString()
        activity?.let {
            it.hideSoftKeyboard()
            viewModel.getEditTaskId()?.let { taskId ->
                viewModel.getSheetSelectedId().let { sheetId ->
                    createUpdateTask(taskId, name, sheetId, description).toEntity()
                        ?.let { taskEntity ->
                            TimeManagerDatabase.getInstance(it)
                                .taskDao()
                                .updateTask(taskEntity)
                        }
                }
            }
        }
        findNavController().navigateUp()
    }

    private fun validateTaskName(): Boolean {
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