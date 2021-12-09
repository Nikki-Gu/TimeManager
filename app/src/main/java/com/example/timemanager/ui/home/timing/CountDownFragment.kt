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
import com.example.timemanager.db.model.createRecord
import java.util.*
import android.media.MediaPlayer

/**
 * A [Fragment] to count down.
 */
@AndroidEntryPoint
class CountDownFragment : Fragment(){
    private var _binding: FragmentCountDownBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler()
    private var totalTime: Long = 0
    private var mCountNum: Long = 0
    private var running = false //计时器是否运行
    private var state = 0 //专注状态 0:未开始/已完成, 1:正在专注, 2:暂停专注

    private lateinit var mediaPlayer: MediaPlayer

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
                    showDialog(R.id.action_navigation_count_down_to_navigation_todo)
                else
                    findNavController().navigate(R.id.action_navigation_count_down_to_navigation_todo)
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
        _binding = FragmentCountDownBinding.inflate(inflater, container, false)
        mediaPlayer = MediaPlayer.create(activity, R.raw.sound)
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
        NavigationUI.setupWithNavController(binding.countDownToolbar, findNavController())
        binding.countDownToolbar.apply {
            title = "倒计时"
            inflateMenu(R.menu.timing_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.count_up -> {
                        if (state != 0)
                            showDialog(0)
                        else
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
                if (state != 0)
                    showDialog(R.id.action_navigation_count_down_to_navigation_todo)
                else
                    findNavController().navigate(R.id.action_navigation_count_down_to_navigation_todo)
            }
        }
    }

    private fun initKeyListener() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(keyListener)
        binding.edHour.isFocusableInTouchMode = true
        binding.edHour.requestFocus()
        binding.edHour.setOnKeyListener(keyListener)
        binding.edMin.isFocusableInTouchMode = true
        binding.edMin.requestFocus()
        binding.edMin.setOnKeyListener(keyListener)
        binding.edSecond.isFocusableInTouchMode = true
        binding.edSecond.requestFocus()
        binding.edSecond.setOnKeyListener(keyListener)
    }

    private fun initTimer() {
        totalTime = 0
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
                binding.edHour.isEnabled = true
                binding.edMin.isEnabled = true
                binding.edSecond.isEnabled = true
                handler.removeCallbacks(countDown)
            }
            else {  //点击"开始/继续专注"
                binding.timingButton.text = "暂停专注"
                binding.finishButton.isVisible = true
                running = true
                state = 1
                binding.edHour.isEnabled = false
                binding.edMin.isEnabled = false
                binding.edSecond.isEnabled = false

                val hour = Integer.valueOf(binding.edHour.text.toString()).toLong()
                val minute = Integer.valueOf(binding.edMin.text.toString()).toLong()
                val seconds = Integer.valueOf(binding.edSecond.text.toString()).toLong()
                mCountNum = hour * 3600 + minute * 60 + seconds
                totalTime = mCountNum
                handler.postDelayed(countDown, 0)
            }
            requireView().requestFocus()
        }

        binding.finishButton.setOnClickListener {
            //点击"完成专注"
            //将本次专注数据存入数据库
            val record = createRecord(0, viewModel.getTimingTaskId()!!, viewModel.getTimingTaskName()!!,
                totalTime - mCountNum, false, Date())
            viewModel.insertRecord(record).observe(viewLifecycleOwner) {}

            binding.timingButton.text = "开始专注"
            binding.finishButton.isVisible = false
            mCountNum = 0
            binding.edHour.setText(getString(R.string.zero))
            binding.edMin.setText(getString(R.string.zero))
            binding.edSecond.setText(getString(R.string.zero))
            running = false
            state = 0
            binding.edHour.isEnabled = true
            binding.edMin.isEnabled = true
            binding.edSecond.isEnabled = true
            handler.removeCallbacks(countDown)

            requireView().requestFocus()
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
                if (totalTime > 0) {
                    val record = createRecord(0, viewModel.getTimingTaskId()!!, viewModel.getTimingTaskName()!!,
                        totalTime, false, Date())
                    viewModel.insertRecord(record).observe(viewLifecycleOwner) {}

                    //播放声音、弹出对话框提醒专注结束
                    mediaPlayer.start()
                    AlertDialog.Builder(context)
                        .setMessage("专注时间到！")
                        .setTitle("提示")
                        .setNegativeButton("好") {_, _ ->
                            mediaPlayer.stop()
                        }
                        .create()
                        .show()
                }
                binding.timingButton.text = "开始专注"
                binding.finishButton.isVisible = false
                binding.edHour.isEnabled = true
                binding.edMin.isEnabled = true
                binding.edSecond.isEnabled = true
                running = false
                state = 0
            }
        }
    }

    private fun showDialog(resId: Int) {
        running = false
        AlertDialog.Builder(context)
            .setMessage("离开此页面将会中断专注，确定要离开吗？")
            .setTitle("提示")
            .setPositiveButton("是") { _, _ ->
                val record = createRecord(
                    0, viewModel.getTimingTaskId()!!, viewModel.getTimingTaskName()!!,
                    mCountNum, true, Date()
                )
                viewModel.insertRecord(record).observe(viewLifecycleOwner) {}
                if (resId == 0)
                    findNavController().navigateUp()
                else
                    findNavController().navigate(resId)
            }
            .setNegativeButton("否") { _, _ ->
                if (state == 1) running = true
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        handler.removeCallbacks(countDown)
        super.onDestroyView()
    }
}