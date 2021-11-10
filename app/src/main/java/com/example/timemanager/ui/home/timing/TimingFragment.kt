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
import androidx.core.view.isVisible
import com.example.timemanager.ui.home.utils.Constants

/**
 * A [Fragment] to timing.
 */
class TimingFragment : Fragment(){
    private var _binding: FragmentTimingBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler()
    private var mCountNum = 0
    private var running = false //计时状态
    private var taskName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimingBinding.inflate(inflater, container, false)
        taskName = arguments?.getString(Constants.TASK_NAME)
        initTimer()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                        findNavController().navigate(R.id.navigation_count_down, Bundle().apply {
                            putString(Constants.TASK_NAME, taskName)
                        })
                        true
                    }
                    else -> false
                }
            }
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                // TODO sheetID
                findNavController().navigate(R.id.navigation_todo)
            }
        }
    }

    private fun initTimer() {
        binding.taskName.text = taskName
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
            binding.timeView.text = "00:00:00"
            running = false
            binding.timeView.isEnabled = true
            handler.removeCallbacks(countUp)
            //TODO: 数据库操作
        }

//        binding.reset.setOnClickListener {
//            running = false
//            binding.timeView.isEnabled = true
//            mCountNum = 0
//        }
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