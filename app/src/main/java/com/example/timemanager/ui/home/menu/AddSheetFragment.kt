package com.example.timemanager.ui.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.example.timemanager.R
import com.example.timemanager.databinding.AddSheetFragmentBinding
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.db.model.createSheet
import com.example.timemanager.db.model.createUpdateSheet
import com.example.timemanager.di.RepositoryModule
import com.example.timemanager.extensions.hideSoftKeyboard
import com.example.timemanager.repository.UserPreferencesRepository
import com.example.timemanager.ui.home.HomeViewModel
import com.example.timemanager.ui.home.HomeViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * a [Fragment] to add Sheet
 */
@AndroidEntryPoint
class AddSheetFragment : Fragment() {
    @Inject
    lateinit var taskDao: TaskDao

    @Inject
    lateinit var sheetDao: SheetDao

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private var _binding: AddSheetFragmentBinding? = null
    private val binding get() = _binding!!

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
        _binding = AddSheetFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initView() {
        initToolBar()
        viewModel.apply {
            if(isEdit()) {
                getEditSheetId()?.let {
                    getSheet(it).observe(
                        viewLifecycleOwner,
                        { sheet ->
                            binding.sheetNameEditText.setText(sheet?.name)
                        }
                    )
                }
            }
        }
    }

    private fun initToolBar() {
        NavigationUI.setupWithNavController(binding.addSheetToolbar, findNavController())
        binding.addSheetToolbar.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            title = if (viewModel.isEdit()) {
                getString(R.string.edit_sheet)
            } else {
                getString(R.string.add_sheet)
            }
            inflateMenu(R.menu.add_sheet_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.save_sheet -> {
                        if (viewModel.isEdit()) {
                            updateSheet()
                        } else {
                            insertSheet()
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }


    private fun insertSheet() {
        if (!validateTaskName()) {
            return
        }
        val name = binding.sheetNameEditText.text.toString()
        viewModel.insertSheet(createSheet(name)).observe(viewLifecycleOwner) {}
        activity?.hideSoftKeyboard()
        findNavController().navigateUp()
    }

    private fun updateSheet() {
        if (!validateTaskName()) {
            return
        }
        val name = binding.sheetNameEditText.text.toString()
        activity?.hideSoftKeyboard()
        viewModel.getEditSheetId()?.let { editSheetId ->
            viewModel.updateSheet(createUpdateSheet(editSheetId, name))
        }
        findNavController().navigateUp()
    }

    private fun validateTaskName(): Boolean {
        return if (binding.sheetNameEditText.text.isNullOrBlank()) {
            binding.sheetNameInput.error = getString(R.string.must_be_not_empty)
            false
        } else true
    }
}
