package com.example.timemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanager.R
import com.example.timemanager.databinding.MenuSheetFragmentBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.repository.mapper.SheetMapper.toDomain
import com.example.timemanager.ui.SwipeController
import com.example.timemanager.ui.home.adapter.SheetsAdapter
import com.example.timemanager.ui.home.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text

@AndroidEntryPoint
class MenuSheetFragment : Fragment() {
    private var _binding: MenuSheetFragmentBinding? = null
    private val binding get() = _binding!!

    // TODO
    private val homeViewModel by viewModels<HomeViewModel>()
    private var sheetId: Int = Constants.DEFAULT_SHEET_ID
    private var sheetsAmount: Int = Constants.DEFAULT_SHEET_AMOUNT

    private val sheetsAdapter = SheetsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MenuSheetFragmentBinding.inflate(inflater, container, false)
        sheetId = arguments?.getInt(Constants.SHEET_ID) ?: Constants.DEFAULT_SHEET_ID
        sheetsAdapter.sheetSelected = sheetId
        sheetsAmount = getSheetAmount()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        initSheetRecycleView()
        setSwipeActions()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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
                // homeViewModel?.setProjectSelectedId(sheet.id)
                sheetsAdapter.sheetSelected = sheet.id
                sheetsAdapter.notifyDataSetChanged()
                findNavController().navigate(R.id.action_navigation_menu_sheet_to_navigation_todo, Bundle().apply {
                    putInt(Constants.SHEET_ID, sheet.id)
                })
            }

            override fun onEditSheetClick(sheet: Sheet) {
                findNavController().navigate(R.id.action_navigation_menu_sheet_to_navigation_add_sheet, Bundle().apply {
                    putInt(Constants.FROM, Constants.EDIT)
                    putInt(Constants.SHEET_ID, sheet.id)
                })
            }
        }
    }

    private fun initToolBar() {
        val toolbar = binding.menuSheetToolbar
        toolbar.apply {
            title = "清单列表"
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                findNavController().navigate(R.id.action_navigation_menu_sheet_to_navigation_todo)
            }
            inflateMenu(R.menu.top_sheet_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.add_sheet -> {
                        // 点击右上角+号
                        findNavController().navigate(R.id.action_navigation_menu_sheet_to_navigation_add_sheet)
                        sheetsAmount = getSheetAmount()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setSwipeActions() {
        val swipeControllerActions =
            if (sheetsAmount > 1) {
                object :
                    SwipeController.SwipeControllerActions {

                    override fun onDelete(position: Int) {
                        sheetsAdapter.currentList[position]?.id?.let { sheetId ->
                            TimeManagerDatabase.getInstance(requireContext())
                                .sheetDao()
                                .deleteSheet(sheetId)
                            initSheetRecycleView()
                            sheetsAmount = getSheetAmount()
                        }
                    }
                }
            } else {
                object :
                    SwipeController.SwipeControllerActions {
                    override fun onDelete(position: Int) {
                        Toast.makeText(requireContext(), "这是最后一个清单了，不能删除哦！", Toast.LENGTH_LONG).show()
                    }
                }
            }
        val swipeController = SwipeController(requireContext(), swipeControllerActions)
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.sheetsRecyclerView)
    }

    private fun getSheetAmount() =
        TimeManagerDatabase.getInstance(requireContext()).sheetDao().getSheets().size
}