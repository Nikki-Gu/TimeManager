package com.example.timemanager

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.timemanager.databinding.ActivityMainBinding
import com.example.timemanager.db.TimeManagerDatabase
import com.example.timemanager.db.TimeManagerDatabase_Impl
import com.example.timemanager.db.dao.SheetDao_Impl
import com.example.timemanager.db.dao.TaskDao_Impl
import com.example.timemanager.db.entity.SheetTasksRelation
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.db.model.Task
import com.example.timemanager.db.model.TaskState
import com.example.timemanager.repository.SheetRepository
import com.example.timemanager.repository.UserPreferencesRepository
import com.example.timemanager.repository.mapper.SheetMapper.toEntity
import com.example.timemanager.repository.mapper.TaskMapper
import com.example.timemanager.repository.mapper.TaskMapper.toEntity
import com.example.timemanager.ui.home.HomeViewModel
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach

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
        initDatabase()
    }

    private fun initDatabase() {
        // 创建数据库
        Room.databaseBuilder(this, TimeManagerDatabase::class.java, "TimeManager")
            .allowMainThreadQueries()
            .build()

        TimeManagerDatabase.getInstance(this).taskDao().deleteAll()
        val taskDao = TimeManagerDatabase.getInstance(this).taskDao()
        val sheetDao = TimeManagerDatabase.getInstance(this).sheetDao()
        val sheet = Sheet(
            id = 1,
            name = "默认清单列表",
            description =  "默认的第一个清单"
        )
        sheet.toEntity()?.let { sheetDao.insertSheet(it)}
        //insert数据
        for (i in (0 until 10)) {
            val task = Task(
                id = i,
                name = "学习$i",
                description = null,
                state = TaskState.DOING,
                sheetId = 1,
                tag = null
            )
            task.toEntity()?.let { taskDao.insertTask(it) }
        }
    }

    private fun initView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 隐藏系统自带的标题栏
        supportActionBar?.hide()

        val navView: BottomNavigationView = binding.navView
        // 底部显示style设置
        navView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

        // 底层跳转逻辑
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
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
}