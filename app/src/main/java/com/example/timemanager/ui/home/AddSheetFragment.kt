package com.example.timemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.timemanager.R
import com.example.timemanager.databinding.AddSheetFragmentBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.model.createSheet
import com.example.timemanager.db.model.createUpdateSheet
import com.example.timemanager.extensions.hideSoftKeyboard
import com.example.timemanager.repository.mapper.SheetMapper.toDomain
import com.example.timemanager.repository.mapper.SheetMapper.toEntity
import com.example.timemanager.ui.home.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

/**
 * a [Fragment] to add Sheet
 */
@AndroidEntryPoint
class AddSheetFragment : Fragment() {
    private var _binding: AddSheetFragmentBinding? = null
    private val binding get() = _binding!!

    // TODO: homeViewModel不是同一个
    private val homeViewModel by viewModels<HomeViewModel>()

    private var jumpFrom: Int? = null
    private var sheetId: Int = Constants.DEFAULT_SHEET_ID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = AddSheetFragmentBinding.inflate(inflater, container, false)
        jumpFrom = arguments?.getInt(Constants.FROM, Constants.ADD)
        sheetId = arguments?.getInt(Constants.SHEET_ID) ?: Constants.DEFAULT_SHEET_ID
        initToolBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (jumpFrom == Constants.EDIT) {
            val sheet = sheetId.let {
                TimeManagerDatabase.getInstance(requireContext()).sheetDao()
                    .getSheet(it).toDomain()
            }
            binding.sheetNameEditText.setText(sheet?.name)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initToolBar() {
        val toolbar = binding.addSheetToolbar
        when (jumpFrom) {
            Constants.ADD -> toolbar.title = getString(R.string.add_sheet)
            Constants.EDIT -> toolbar.title = getString(R.string.edit_sheet)
            else -> toolbar.title = getString(R.string.add_sheet)
        }
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        NavigationUI.setupWithNavController(binding.addSheetToolbar, findNavController())
        binding.addSheetToolbar.apply {
            inflateMenu(R.menu.add_sheet_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.save_sheet -> {
                        when (jumpFrom) {
                            Constants.ADD -> insertSheet()
                            Constants.EDIT -> updateSheet()
                            else -> insertSheet()
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
            createUpdateSheet(sheetId, name).toEntity()?.let { sheetEntity ->
                TimeManagerDatabase.getInstance(it)
                    .sheetDao()
                    .updateSheet(sheetEntity)
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
