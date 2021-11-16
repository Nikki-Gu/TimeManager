package com.example.timemanager

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.timemanager.databinding.ActivityMainBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.model.createSheet
import com.example.timemanager.db.model.createTask
import com.example.timemanager.repository.mapper.SheetMapper.toEntity
import com.example.timemanager.repository.mapper.TaskMapper.toEntity
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDatabase()
        initView()
        initNavController()
        setNavViewVisibility()
    }

    private fun initView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 隐藏系统自带的标题栏
        supportActionBar?.hide()
        val navView: BottomNavigationView = binding.navView
        // 底部显示style设置
        navView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
    }

    // 底层跳转逻辑
    private fun initNavController() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home_navigation,
                R.id.navigation_analysis,
                R.id.navigation_pet,
                R.id.navigation_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun initDatabase() {
        Room.databaseBuilder(this, TimeManagerDatabase::class.java, "TimeManager")
            .build()

        // 判断是否第一次使用该app
        val sharedPreferences = this.getSharedPreferences("preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val isFirstIn = sharedPreferences.getBoolean("isFirstRun", true)
        if (isFirstIn) {
            editor.putBoolean("isFirstRun", false)
            editor.apply()
            // 第一次使用app时显示使用指南
            val taskDao = TimeManagerDatabase.getInstance(this).taskDao()
            val sheetDao = TimeManagerDatabase.getInstance(this).sheetDao()
            createSheet("默认清单列表").toEntity()?.let { runBlocking { sheetDao.insertSheet(it) } }
            createTask("向右滑动删除", 1).toEntity()?.let { runBlocking { taskDao.insertTask(it) } }
            createTask("点击右边图标编辑待办", 1).toEntity()?.let { runBlocking { taskDao.insertTask(it) } }
            createTask("点击左边图标标记待办为已完成", 1).toEntity()
                ?.let { runBlocking { taskDao.insertTask(it) } }
            createTask("点击待办进入计时页面", 1).toEntity()
                ?.let { runBlocking { taskDao.insertTask(it) } }
        }
    }

    private fun setNavViewVisibility() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_todo,
                R.id.navigation_analysis,
                R.id.navigation_pet,
                R.id.navigation_setting,
                R.id.home_navigation -> {
                    binding.navView.visibility = View.VISIBLE
                }
                else -> {
                    binding.navView.visibility = View.GONE
                }
            }
        }
    }
}