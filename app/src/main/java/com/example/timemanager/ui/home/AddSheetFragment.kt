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
import com.example.timemanager.databinding.AddSheetFragmentBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.model.createSheet
import com.example.timemanager.db.model.createUpdateSheet
import com.example.timemanager.extensions.hideSoftKeyboard
import com.example.timemanager.repository.mapper.SheetMapper.toDomain
import com.example.timemanager.repository.mapper.SheetMapper.toEntity
import dagger.hilt.android.AndroidEntryPoint

/**
 * a [Fragment] to add Sheet
 */
@AndroidEntryPoint
class AddSheetFragment : Fragment() {
    private var _binding: AddSheetFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by navGraphViewModels(R.id.home_navigation)

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
        if (viewModel.isEdit()) {
            val sheet = viewModel.getEditSheetId()?.let {
                TimeManagerDatabase.getInstance(requireContext()).sheetDao()
                    .getSheet(it).toDomain()
            }
            binding.sheetNameEditText.setText(sheet?.name)
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
        activity?.let {
            it.hideSoftKeyboard()
            createSheet(name).toEntity()?.let { sheetEntity ->
                TimeManagerDatabase.getInstance(it)
                    .sheetDao()
                    .insertSheet(sheetEntity)
            }
        }
        findNavController().navigateUp()
    }

    private fun updateSheet() {
        if (!validateTaskName()) {
            return
        }
        val name = binding.sheetNameEditText.text.toString()
        activity?.let {
            it.hideSoftKeyboard()
            viewModel.getEditSheetId()?.let { editSheetId ->
                createUpdateSheet(editSheetId, name).toEntity()?.let { sheetEntity ->
                    TimeManagerDatabase.getInstance(it)
                        .sheetDao()
                        .updateSheet(sheetEntity)
                }
            }
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
