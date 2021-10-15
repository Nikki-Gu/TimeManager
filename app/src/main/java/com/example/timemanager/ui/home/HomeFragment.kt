package com.example.timemanager.ui.home

import android.content.Intent
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanager.AddTodoActivity
import com.example.timemanager.R
import com.example.timemanager.databinding.FragmentHomeBinding

class HomeFragment : Fragment(){

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val dataset = ArrayList<TodoItem>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        // toolbar设置
        toolbar.title = "默认清单列表"
        toolbar.overflowIcon = resources.getDrawable(R.drawable.ic_list_setting)
        toolbar.inflateMenu(R.menu.top_home_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.list_add -> {
                    // Navigate to item settings view
                    val intent = Intent(context, AddTodoActivity::class.java)
                    startActivity(intent)
                    true
                }
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

        loadTodoList()
        // recycler
        val recycler = binding.recyclerView
        // set manager
        val recyclerManager = LinearLayoutManager(this.activity)
        recycler.layoutManager = recyclerManager
        // set adapter
        val todoAdapter = TodoAdapter(dataset)
        recycler.adapter = todoAdapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveTodoList()
        _binding = null
    }

    fun loadTodoList(){
        // fragment初始化时，读取todolist，保存到dataset中
        dataset.clear()
        // 目前只读取两个测试数据
        dataset.add(TodoItem("foo"))
        dataset.add(TodoItem("bar"))
    }

    fun saveTodoList(){
        // fragment销毁时，保存dataset
        // 目前是空实现
    }
}