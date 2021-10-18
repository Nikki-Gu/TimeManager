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
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.db.model.Task
import com.example.timemanager.db.model.TaskState
import com.example.timemanager.repository.mapper.SheetMapper.toEntity
import com.example.timemanager.repository.mapper.TaskMapper.toEntity
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

//    private val homeViewModel = HomeViewModel(
//            TaskDao_Impl(TimeManagerDatabase_Impl()),
//            UserPreferencesRepository(this),
//            SheetRepository(SheetDao_Impl(TimeManagerDatabase_Impl()))
//        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initNavController()
        initDatabase()
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
                R.id.navigation_todo,
                R.id.navigation_analysis,
                R.id.navigation_pet,
                R.id.navigation_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun initDatabase() {
        // 创建数据库
        Room.databaseBuilder(this, TimeManagerDatabase::class.java, "TimeManager")
            .allowMainThreadQueries()
            .build()

        val taskDao = TimeManagerDatabase.getInstance(this).taskDao()
        val sheetDao = TimeManagerDatabase.getInstance(this).sheetDao()
        val sheet = Sheet(
            id = 1,
            name = "默认清单列表",
            description =  "默认的第一个清单"
        )
        sheet.toEntity()?.let { sheetDao.insertSheet(it)}
        //insert数据
        val task1 = Task(
            name = "向右滑动删除",
            description = null,
            state = TaskState.DOING,
            sheetId = 1,
            tag = null
        )
        task1.toEntity()?.let { taskDao.insertTask(it) }
        val task2 = Task(
            name = "点击右边图标编辑待办",
            description = null,
            state = TaskState.DOING,
            sheetId = 1,
            tag = null
        )
        task2.toEntity()?.let { taskDao.insertTask(it) }
        val task3 = Task(
            name = "点击左边图标标记待办为已完成",
            description = null,
            state = TaskState.DOING,
            sheetId = 1,
            tag = null
        )
        task3.toEntity()?.let { taskDao.insertTask(it) }
    }

    private fun setNavViewVisibility() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_todo,
                R.id.navigation_analysis,
                R.id.navigation_pet,
                R.id.navigation_setting -> {
                    binding.navView.visibility = View.VISIBLE
                }
                else -> {
                    binding.navView.visibility = View.GONE
                }
            }
        }
    }
}