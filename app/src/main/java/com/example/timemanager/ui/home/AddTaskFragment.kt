package com.example.timemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.example.timemanager.R
import com.example.timemanager.databinding.AddTaskFragmentBinding
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.di.RepositoryModule
import com.example.timemanager.extensions.hideSoftKeyboard
import com.example.timemanager.repository.UserPreferencesRepository
import com.example.timemanager.ui.home.adapter.CustomizedSpinnerAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject



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
    lateinit var userPreferencesRepository: UserPreferencesRepository

    var spinner_priority: Spinner? = null
    var priority: List<String> = ArrayList()

    private val viewModel: HomeViewModel by navGraphViewModels(R.id.home_navigation) {
        HomeViewModelFactory(
            taskRepository = RepositoryModule.provideTaskRepository(taskDao),
            sheetRepository = RepositoryModule.provideSheetRepository(sheetDao),
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
        viewModel.apply {
            if(isEdit()) {
                getEditTaskId()?.let {
                    getTask(it).observe(
                        viewLifecycleOwner,
                        { task ->
                            binding.taskNameEditText.setText(task?.name)
                            binding.taskNameEditText.setText(task?.name)
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
            spinner_priority = findViewById(R.id.spinner_priority)
            spinner()
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
        viewModel.insertTask(name, description).observe(viewLifecycleOwner) {}
        activity?.hideSoftKeyboard()
        findNavController().navigateUp()
    }

    private fun updateTask() {
        if (!validateTaskName()) {
            return
        }
        val name = binding.taskNameEditText.text.toString()
        val description = binding.taskDescriptionEditText.text.toString()
        activity?.hideSoftKeyboard()
        viewModel.getEditTaskId()?.let { taskId ->
            viewModel.updateTask(taskId, name, description)
        }
        findNavController().navigateUp()
    }

    private fun validateTaskName(): Boolean {
        return if (binding.taskNameEditText.text.isNullOrBlank()) {
            binding.taskNameInput.error = getString(R.string.must_be_not_empty)
            false
        } else true
    }

    private fun spinner() {
        priority+="紧急重要"
        priority+="重要而不紧急"
        priority+="紧急而不重要"
        priority+="不紧急不重要"
        priority+="请选择优先级"
        val spinneradapter = CustomizedSpinnerAdapter(this.context, R.layout.support_simple_spinner_dropdown_item, priority)
        spinner_priority!!.adapter = spinneradapter
        //默认选中最后一项
        spinner_priority!!.setSelection(priority.size - 1, true)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}