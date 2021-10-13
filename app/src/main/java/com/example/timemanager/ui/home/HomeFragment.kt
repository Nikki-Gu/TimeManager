package com.example.timemanager.ui.home

import android.content.Intent
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.timemanager.AddTodoActivity
import com.example.timemanager.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toolbar = binding.homeToolbar
        // textview，后续删掉
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        // toolbar设置
        // toolbar.setNavigationIcon(R.drawable.ic_setting)
        toolbar.title = "默认清单列表"
        toolbar.overflowIcon = resources.getDrawable(R.drawable.ic_list_setting)
        toolbar.inflateMenu(R.menu.top_home_menu)
        toolbar.setNavigationOnClickListener {
            when(it.id) {
                R.id.list_add -> {
                    // Navigate to item settings view
                    true
                }
                else -> false
            }
        }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.list_setting -> {
                    // Navigate to item settings view
                    true
                }
                R.id.list_delete -> {
                    // delete list
                    true
                }
                else -> false
            }
        }
        return root
    }


        // todoListAdd按钮的监听器
        binding.homeInclude.todoListAdd.setOnClickListener(this)

        return root
    }

    override fun onClick(p0: View?) {
        val intent = Intent(context, AddTodoActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}