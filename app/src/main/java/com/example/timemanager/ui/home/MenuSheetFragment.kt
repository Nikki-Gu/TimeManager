package com.example.timemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanager.R
import com.example.timemanager.databinding.MenuSheetFragmentBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.repository.mapper.SheetMapper.toDomain
import com.example.timemanager.ui.home.adapter.SheetsAdapter
import com.example.timemanager.ui.home.utils.Constants

class MenuSheetFragment : Fragment() {
    private var _binding: MenuSheetFragmentBinding? = null
    private val binding get() = _binding!!

    // TODO
    private val homeViewModel by viewModels<HomeViewModel>()

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
        initToolBar()
        initSheetRecycleView()
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
                homeViewModel?.setProjectSelectedId(sheet.id)
            }

            override fun onEditSheetClick(sheet: Sheet) {
                findNavController().navigate(R.id.navigation_add_sheet, Bundle().apply {
                    putInt(Constants.FROM, Constants.EDIT)
                })
            }
        }

        homeViewModel?.projectSelectedId?.observe(
            viewLifecycleOwner,
            {
                sheetsAdapter.apply {
                    sheetSelected = it
                    notifyDataSetChanged() // 不一定使用上了，recycleview里面不知道有没有设置观察者模式
                }

            }
        )
    }

    private fun initToolBar() {
        val toolbar = binding.menuSheetToolbar
        toolbar.apply {
            title = "清单列表"
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                findNavController().navigate(R.id.navigation_todo)
            }
            inflateMenu(R.menu.top_sheet_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.add_sheet -> {
                        // 点击右上角+号
                        findNavController().navigate(R.id.navigation_add_sheet)
                        true
                    }
                    else -> false
                }
            }
        }
    }
}