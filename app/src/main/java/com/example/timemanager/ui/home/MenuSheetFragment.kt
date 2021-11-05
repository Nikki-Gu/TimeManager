package com.example.timemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanager.R
import com.example.timemanager.databinding.MenuSheetFragmentBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.repository.mapper.SheetMapper.toDomain
import com.example.timemanager.ui.SwipeController
import com.example.timemanager.ui.home.adapter.SheetsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuSheetFragment : Fragment() {
    private var _binding: MenuSheetFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by navGraphViewModels(R.id.home_navigation)

    private val sheetsAdapter = SheetsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MenuSheetFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sheetsAdapter.sheetSelected = viewModel.getSheetSelectedId()
        initToolBar()
        initSheetRecycleView()
        setSwipeActions()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initToolBar() {
        val toolbar = binding.menuSheetToolbar
        toolbar.apply {
            title = getString(R.string.menu_sheet)
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                findNavController().navigate(R.id.action_navigation_menu_sheet_to_navigation_todo)
            }
            inflateMenu(R.menu.top_sheet_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.add_sheet -> {
                        findNavController().navigate(R.id.action_navigation_menu_sheet_to_navigation_add_sheet)
                        viewModel.setEdit(false)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initSheetRecycleView() {
        binding.sheetsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sheetsAdapter
        }
        // TODO 数据库相关变化-待优化为观察者模式
        val sheetsList =
            TimeManagerDatabase.getInstance(requireContext()).sheetDao().getSheets().map {
                it.toDomain()
            }
        sheetsAdapter.submitList(sheetsList)

        sheetsAdapter.sheetClickListener = object : SheetsAdapter.SheetClickListener {
            override fun onSheetClick(sheet: Sheet) {
                viewModel.setSheetSelectedId(sheet.id)
                sheetsAdapter.sheetSelected = sheet.id
                sheetsAdapter.notifyDataSetChanged()
                findNavController().navigateUp()
            }

            override fun onEditSheetClick(sheet: Sheet) {
                findNavController().navigate(R.id.action_navigation_menu_sheet_to_navigation_add_sheet)
                viewModel.setEdit(true)
                viewModel.setEditSheetId(sheet.id)
            }
        }
    }

    // TODO 最后一个不能删除
    private fun setSwipeActions() {
        val swipeControllerActions =
            object :
                SwipeController.SwipeControllerActions {

                override fun onDelete(position: Int) {
                    sheetsAdapter.currentList[position]?.id?.let { it ->
                        TimeManagerDatabase.getInstance(requireContext())
                            .sheetDao()
                            .deleteSheet(it)
                        initSheetRecycleView()
                    }
                }
            }
        val swipeController = SwipeController(requireContext(), swipeControllerActions)
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.sheetsRecyclerView)
    }

}