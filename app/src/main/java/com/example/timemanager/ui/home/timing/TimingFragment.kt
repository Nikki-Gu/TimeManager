package com.example.timemanager.ui.home.timing

import android.app.AlertDialog
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
import com.example.timemanager.db.dao.RecordDao
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.db.model.createRecord
import com.example.timemanager.di.RepositoryModule
import com.example.timemanager.repository.UserPreferencesRepository
import com.example.timemanager.ui.home.HomeViewModel
import com.example.timemanager.ui.home.HomeViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

/**
 * A [Fragment] to timing.
 */
@AndroidEntryPoint
class TimingFragment : Fragment(){
    private var _binding: FragmentTimingBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler()
    private var mCountNum:Long = 0
    private var running = false //计时器是否运行
    private var state = 0 //专注状态 0:未开始/已完成, 1:正在专注, 2:暂停专注

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

    private val keyListener = View.OnKeyListener { v: View?, keyCode: Int, keyEvent: KeyEvent ->
        if (keyEvent.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {  //按返回键
                if (state != 0)
                    showDialog(0)
                else
                    findNavController().navigateUp()
                return@OnKeyListener true
            }
        }
        false
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
        initKeyListener()
        initTimer()
    }

    override fun onResume() {
        //若是第一次进入前台, 不弹窗;
        //若是从后台回到前台(即从中断中回来), 弹出提示框询问是否继续计时
        super.onResume()
        if (state != 0) {
            AlertDialog.Builder(context)
                .setMessage("您刚才离开了此页面，专注暂停，是否继续专注？")
                .setTitle("提示")
                .setPositiveButton("是") { _, _ ->
                    if (state == 1) running = true
                }
                .setNegativeButton("否") { _, _ ->
                    val record = createRecord(
                        0, viewModel.getTimingTaskId()!!, viewModel.getTimingTaskName()!!,
                        mCountNum, true, Date()
                    )
                    viewModel.insertRecord(record).observe(viewLifecycleOwner) {}
                    findNavController().navigate(R.id.action_navigation_timing_to_navigation_todo)
                }
                .create()
                .show()
        }
    }

    override fun onPause() {
        //离开前台, 计时暂停
        super.onPause()
        running = false
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
                        if (state != 0)  //如果专注还未完成, 跳出提示框
                            showDialog(R.id.action_navigation_timing_to_navigation_count_down)
                        else  //否则直接跳转
                            findNavController().navigate(R.id.action_navigation_timing_to_navigation_count_down)
                        true
                    }
                    else -> false
                }
            }
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                if (state != 0)  //如果专注还未完成, 跳出提示框
                    showDialog(R.id.action_navigation_timing_to_navigation_todo)
                else  //否则直接返回
                    findNavController().navigate(R.id.action_navigation_timing_to_navigation_todo)
            }
        }
    }

    private fun initKeyListener() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(keyListener)
    }

    private fun initTimer() {
        mCountNum = 0
        running = false
        state = 0
        binding.taskName.text = viewModel.getTimingTaskName()
        binding.timingButton.text = "开始专注"
        binding.finishButton.text = "完成专注"
        binding.finishButton.isVisible = false

        binding.timingButton.setOnClickListener {
            if (running) {  //点击"暂停专注"
                binding.timingButton.text = "继续专注"
                running = false
                state = 2
                binding.timeView.isEnabled = true
                handler.removeCallbacks(countUp)
            }
            else {  //点击"开始/继续专注"
                binding.timingButton.text = "暂停专注"
                binding.finishButton.isVisible = true
                running = true
                state = 1
                binding.timeView.isEnabled = false
                handler.postDelayed(countUp, 0)
            }
            requireView().requestFocus()
        }

        binding.finishButton.setOnClickListener {
            //点击"完成专注"
            //将本次专注数据存入数据库
            val record = createRecord(0, viewModel.getTimingTaskId()!!, viewModel.getTimingTaskName()!!,
                mCountNum, false, Date())
            viewModel.insertRecord(record).observe(viewLifecycleOwner) {}

            binding.timingButton.text = "开始专注"
            binding.finishButton.isVisible = false
            mCountNum = 0
            binding.timeView.text = getString(R.string.initial_time)
            running = false
            state = 0
            binding.timeView.isEnabled = true
            handler.removeCallbacks(countUp)

            requireView().requestFocus()
        }
    }

    private val countUp = object : Runnable {
        override fun run() {
            if (running) {
                mCountNum++
            }
            val hour = mCountNum / 3600 % 24
            val minute = mCountNum % 3600 / 60
            val time = String.format("%02d:%02d:%02d", hour, minute, mCountNum % 60)
            binding.timeView.text = time
            handler.postDelayed(this, 1000)
        }
    }

    private fun showDialog(resId: Int) {
        //停止计时
        running = false
        //弹出提示框
        AlertDialog.Builder(context)
            .setMessage("离开此页面将会中断专注，确定要离开吗？")
            .setTitle("提示")
            .setPositiveButton("是") { _, _ ->
                //若确定离开
                //将此次专注记录记入数据库, 并且标记为"中断"
                val record = createRecord(
                    0, viewModel.getTimingTaskId()!!, viewModel.getTimingTaskName()!!,
                    mCountNum, true, Date()
                )
                viewModel.insertRecord(record).observe(viewLifecycleOwner) {}
                //跳转回todo界面
                if (resId == 0)
                    findNavController().navigateUp()
                else
                    findNavController().navigate(resId)
            }
            .setNegativeButton("否") { _, _ ->
                //若不离开, 仍然在计时界面
                //state==1: 之前没有点暂停 => 继续计时
                //state==2: 之前点了暂停 => 等用户点继续再开始计时
                if (state == 1) running = true
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        handler.removeCallbacks(countUp)
        super.onDestroyView()
    }
}