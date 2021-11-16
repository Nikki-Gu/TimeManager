package com.example.timemanager.ui.home.timing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.timemanager.R
import com.example.timemanager.databinding.FragmentTimingBinding
import android.os.Handler
import android.view.KeyEvent
import androidx.core.view.isVisible
import androidx.navigation.navGraphViewModels
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.di.RepositoryModule
import com.example.timemanager.repository.UserPreferencesRepository
import com.example.timemanager.ui.home.HomeViewModel
import com.example.timemanager.ui.home.HomeViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A [Fragment] to timing.
 */
@AndroidEntryPoint
class TimingFragment : Fragment(){
    private var _binding: FragmentTimingBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler()
    private var mCountNum = 0
    private var running = false //计时状态

    @Inject
    lateinit var taskDao: TaskDao

    @Inject
    lateinit var sheetDao: SheetDao

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

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
        _binding = FragmentTimingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initTimer()
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v: View?, keyCode: Int, event: KeyEvent ->
            //判断用户点击了手机自带的返回键
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().navigate(R.id.action_navigation_timing_to_navigation_todo)
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun initToolbar() {
        NavigationUI.setupWithNavController(binding.timingToolbar, findNavController())
        binding.timingToolbar.apply {
            title = "正计时"
            inflateMenu(R.menu.timing_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.count_up -> {
                        true
                    }
                    R.id.count_down -> {
                        findNavController().navigate(R.id.action_navigation_timing_to_navigation_count_down)
                        true
                    }
                    else -> false
                }
            }
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                findNavController().navigate(R.id.action_navigation_timing_to_navigation_todo)
            }
        }
    }

    private fun initTimer() {
        binding.taskName.text = viewModel.getTimingTaskName()
        binding.timingButton.text = "开始专注"
        binding.finishButton.text = "完成专注"
        binding.finishButton.isVisible = false

        binding.timingButton.setOnClickListener {
            if (running) {
                binding.timingButton.text = "继续专注"
                running = false
                binding.timeView.isEnabled = true
                handler.removeCallbacks(countUp)
            }
            else {
                binding.timingButton.text = "暂停专注"
                binding.finishButton.isVisible = true
                running = true
                binding.timeView.isEnabled = false
                handler.postDelayed(countUp, 0)
            }
        }

        binding.finishButton.setOnClickListener {
            binding.timingButton.text = "开始专注"
            binding.finishButton.isVisible = false
            mCountNum = 0
            binding.timeView.text = getString(R.string.initial_time)
            running = false
            binding.timeView.isEnabled = true
            handler.removeCallbacks(countUp)
            //TODO: 数据库操作
        }
    }

    private val countUp = object : Runnable {
        override fun run() {
            if (running) {
                mCountNum++
            }
            val hour: Int = mCountNum / 3600 % 24
            val minute: Int = mCountNum % 3600 / 60
            val time = String.format("%02d:%02d:%02d", hour, minute, mCountNum % 60)
            binding.timeView.text = time
            handler.postDelayed(this, 1000)
        }
    }

    override fun onDestroyView() {
        _binding = null
        handler.removeCallbacks(countUp)
        super.onDestroyView()
    }
}