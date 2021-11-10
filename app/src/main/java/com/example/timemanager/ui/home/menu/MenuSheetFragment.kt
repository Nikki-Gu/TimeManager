package com.example.timemanager.ui.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanager.R
import com.example.timemanager.databinding.MenuSheetFragmentBinding
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.di.RepositoryModule
import com.example.timemanager.repository.UserPreferencesRepository
import com.example.timemanager.ui.SwipeController
import com.example.timemanager.ui.home.HomeViewModel
import com.example.timemanager.ui.home.HomeViewModelFactory
import com.example.timemanager.ui.home.adapter.SheetsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MenuSheetFragment : Fragment() {
    @Inject
    lateinit var taskDao: TaskDao

    @Inject
    lateinit var sheetDao: SheetDao

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private var _binding: MenuSheetFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by navGraphViewModels(R.id.home_navigation) {
        HomeViewModelFactory(
            taskRepository = RepositoryModule.provideTaskRepository(taskDao),
            sheetRepository = RepositoryModule.provideSheetRepository(sheetDao),
            userPreferencesRepository = userPreferencesRepository
        )
    }

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
        sheetsAdapter.sheetSelected = viewModel.sheetSelectedId.value
        initToolBar()
        initSheetRecycleView()
        initSheetSelectedIdObserver()
        initSheetClickListener()
        initDeleteSheetObserver()
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
        viewModel.sheets.observe(
            viewLifecycleOwner,
            {
                sheetsAdapter.submitList(it)
            }
        )
    }

    private fun initSheetSelectedIdObserver() {
        viewModel.sheetSelectedId.observe(
            viewLifecycleOwner,
            {
                sheetsAdapter.sheetSelected = it
                sheetsAdapter.notifyDataSetChanged()
            }
        )
    }

    private fun initSheetClickListener() {
        sheetsAdapter.sheetClickListener = object : SheetsAdapter.SheetClickListener {
            override fun onSheetClick(sheet: Sheet) {
                viewModel.setSheetSelectedId(sheet.id)
                findNavController().navigateUp()
            }

            override fun onEditSheetClick(sheet: Sheet) {
                findNavController().navigate(R.id.action_navigation_menu_sheet_to_navigation_add_sheet)
                viewModel.setEdit(true)
                viewModel.setEditSheetId(sheet.id)
            }
        }
    }

    private fun initDeleteSheetObserver() {
        val swipeController = SwipeController(requireContext(), object :
            SwipeController.SwipeControllerActions {

            override fun onDelete(position: Int) {
                viewModel.sheets.observe(
                    viewLifecycleOwner,
                    {
                        it?.let { list ->
                            if (list.size > 1) {
                                sheetsAdapter.currentList[position]?.id?.let { it ->
                                    viewModel.deleteSheet(it)
                                }
                            }
                        }
                    }
                )
                if(viewModel.sheets.value?.size == 1) {
                    Toast.makeText(requireContext(), "这是最后一个清单，不能删除哦！", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.sheetsRecyclerView)
    }

}