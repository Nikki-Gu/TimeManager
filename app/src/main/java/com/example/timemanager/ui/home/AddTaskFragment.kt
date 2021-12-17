package com.example.timemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.example.timemanager.databinding.AddTaskFragmentBinding
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.di.RepositoryModule
import com.example.timemanager.extensions.hideSoftKeyboard
import com.example.timemanager.repository.UserPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import android.widget.ArrayAdapter
import com.example.timemanager.R
import com.example.timemanager.db.dao.RecordDao


/**
 * A [Fragment] to create task.
 */
@AndroidEntryPoint
class AddTaskFragment : Fragment() {
    private var _binding: AddTaskFragmentBinding? = null
    private val binding get() = _binding!!

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
        initSpinner()
        viewModel.apply {
            if (isEdit()) {
                getEditTaskId()?.let {
                    getTask(it).observe(
                        viewLifecycleOwner,
                        { task ->
                            binding.taskNameEditText.setText(task?.name)
                            binding.rank.setSelection(task?.rank ?: 4)
                            binding.taskDescriptionEditText.setText(task?.description)
                        }
                    )
                }
            }
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

    private fun initSpinner() {
        val adapter: ArrayAdapter<String> =
            object : ArrayAdapter<String>(requireContext(), R.layout.spinner_optional_item) {
                override fun getCount(): Int {
                    return super.getCount() - 1
                }
            }
        adapter.apply {
            add(getString(R.string.rank0))
            add(getString(R.string.rank1))
            add(getString(R.string.rank2))
            add(getString(R.string.rank3))
            add(getString(R.string.hint_of_rank))
        }
        binding.rank.adapter = adapter
        // 设置默认选中最后一项
        binding.rank.setSelection(adapter.count, true)
    }

    private fun insertTask() {
        if (!validateTaskName()) {
            return
        }
        val name = binding.taskNameEditText.text.toString()
        val description = binding.taskDescriptionEditText.text.toString()
        val rank = when (binding.rank.selectedItem.toString()) {
            getString(R.string.rank0) -> 0
            getString(R.string.rank1) -> 1
            getString(R.string.rank2) -> 2
            getString(R.string.rank3) -> 3
            else -> 4
        }
        viewModel.insertTask(name, description, rank).observe(viewLifecycleOwner) {}
        activity?.hideSoftKeyboard()
        findNavController().navigateUp()
    }

    private fun updateTask() {
        if (!validateTaskName()) {
            return
        }
        val name = binding.taskNameEditText.text.toString()
        val description = binding.taskDescriptionEditText.text.toString()
        val rank = when (binding.rank.selectedItem.toString()) {
            getString(R.string.rank0) -> 0
            getString(R.string.rank1) -> 1
            getString(R.string.rank2) -> 2
            getString(R.string.rank3) -> 3
            else -> 4
        }
        activity?.hideSoftKeyboard()
        viewModel.getEditTaskId()?.let { taskId ->
            viewModel.updateTask(taskId, name, description, rank)
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