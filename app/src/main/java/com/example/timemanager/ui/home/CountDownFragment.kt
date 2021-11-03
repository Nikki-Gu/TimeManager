package com.example.timemanager.ui

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
import com.example.timemanager.databinding.FragmentCountDownBinding

/**
 * A [Fragment] to count down.
 */
class CountDownFragment : Fragment(){
    private var _binding: FragmentCountDownBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler()
    private var mCountNum = 0
    private var running = false //计时状态

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountDownBinding.inflate(inflater, container, false)
        initTimer()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationUI.setupWithNavController(binding.countDownToolbar, findNavController())
        binding.countDownToolbar.apply {
            title = "倒计时"
            inflateMenu(R.menu.timing_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.count_up -> {
                        findNavController().navigate(R.id.navigation_timing)
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
                findNavController().navigate(R.id.navigation_todo)
            }
        }
    }

    private fun initTimer() {
        //TODO: 显示任务名称

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
            binding.edHour.setText("00")
            binding.edMin.setText("00")
            binding.edSecond.setText("00")
            running = false
            binding.edHour.isEnabled = true
            binding.edMin.isEnabled = true
            binding.edSecond.isEnabled = true
            handler.removeCallbacks(countDown)
            //TODO: 数据库操作
        }

//        binding.reset.setOnClickListener {
//            running = false
//            binding.edHour.isEnabled = true
//            binding.edMin.isEnabled = true
//            binding.edSecond.isEnabled = true
//            mCountNum = 60
//        }
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
                //TODO
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