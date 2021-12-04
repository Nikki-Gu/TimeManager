package com.example.timemanager.ui.home.timing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.timemanager.R
import android.os.Handler
import androidx.core.view.isVisible
import androidx.navigation.navGraphViewModels
import com.example.timemanager.databinding.FragmentCountDownBinding
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.di.RepositoryModule
import com.example.timemanager.repository.UserPreferencesRepository
import com.example.timemanager.ui.home.HomeViewModel
import com.example.timemanager.ui.home.HomeViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.view.KeyEvent
import com.example.timemanager.db.dao.RecordDao

/**
 * A [Fragment] to count down.
 */
@AndroidEntryPoint
class CountDownFragment : Fragment(){
    private var _binding: FragmentCountDownBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler()
    private var mCountNum = 0
    private var running = false //计时状态

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
        _binding = FragmentCountDownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initTimer()
    }

    override fun onResume() {
        super.onResume()
        requireView().setFocusableInTouchMode(true)
        requireView().requestFocus()
        requireView().setOnKeyListener { v: View?, keyCode: Int, event: KeyEvent ->
            //判断用户点击了手机自带的返回键
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().navigate(R.id.action_navigation_count_down_to_navigation_todo)
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun initToolbar() {
        NavigationUI.setupWithNavController(binding.countDownToolbar, findNavController())
        binding.countDownToolbar.apply {
            title = "倒计时"
            inflateMenu(R.menu.timing_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.count_up -> {
                        findNavController().navigateUp()
                        true
                    }
                    R.id.count_down -> {
                        true
                    }
                    else -> false
                }
            }
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                findNavController().navigate(R.id.action_navigation_count_down_to_navigation_todo)
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
                binding.edHour.isEnabled = true
                binding.edMin.isEnabled = true
                binding.edSecond.isEnabled = true
                handler.removeCallbacks(countDown)
            }
            else {
                binding.timingButton.text = "暂停专注"
                binding.finishButton.isVisible = true
                running = true
                binding.edHour.isEnabled = false
                binding.edMin.isEnabled = false
                binding.edSecond.isEnabled = false

                val hour: Int = Integer.valueOf(binding.edHour.text.toString())
                val minute: Int = Integer.valueOf(binding.edMin.text.toString())
                val seconds: Int = Integer.valueOf(binding.edSecond.text.toString())
                mCountNum = hour * 3600 + minute * 60 + seconds
                handler.postDelayed(countDown, 0)
            }
        }

        binding.finishButton.setOnClickListener {
            binding.timingButton.text = "开始专注"
            binding.finishButton.isVisible = false
            mCountNum = 0
            binding.edHour.setText(getString(R.string.zero))
            binding.edMin.setText(getString(R.string.zero))
            binding.edSecond.setText(getString(R.string.zero))
            running = false
            binding.edHour.isEnabled = true
            binding.edMin.isEnabled = true
            binding.edSecond.isEnabled = true
            handler.removeCallbacks(countDown)
            //TODO: 数据库操作
        }
    }

    private val countDown = object : Runnable {
        override fun run() {
            binding.edHour.setText(String.format("%02d", mCountNum / 3600 % 24))
            binding.edMin.setText(String.format("%02d", mCountNum % 3600 / 60))
            binding.edSecond.setText(String.format("%02d", mCountNum % 60))

            if (mCountNum > 0) {
                handler.postDelayed(this, 1000)
                if (running) {
                    mCountNum--
                }
            } else {
                binding.timingButton.text = "开始专注"
                binding.finishButton.isVisible = false
                binding.edHour.isEnabled = true
                binding.edMin.isEnabled = true
                binding.edSecond.isEnabled = true
                running = false
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        handler.removeCallbacks(countDown)
        super.onDestroyView()
    }
}