package com.example.timemanager.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.timemanager.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private lateinit var settingViewModel: SettingViewModel
    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.
//        settingViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        val toolbar = binding.homeToolbar

        val textView: TextView = binding.textSetting
        settingViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        // toolbar设置
        toolbar.title = "个人设置"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}