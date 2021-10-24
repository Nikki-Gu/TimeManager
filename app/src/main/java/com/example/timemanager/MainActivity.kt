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
        // TODO 第一次安装app的时候显示下面的提示待办，之后不显示
        TimeManagerDatabase.getInstance(this).taskDao().deleteAll() // 需要修改
        val taskDao = TimeManagerDatabase.getInstance(this).taskDao()
        val sheetDao = TimeManagerDatabase.getInstance(this).sheetDao()
        val sheet = Sheet(
            id = 1,
            name = "默认清单列表测试_byMirack",
            description = null,
            sheetClass = "测试类别"
        )
        val sheet2 = Sheet(
            id = 2,
            name = "默认清单列表测试_2",
            description = null,
            sheetClass = "测试类别2"
        )
        println("SHEET INSERT 1")
        sheet.toEntity()?.let { sheetDao.insertSheet(it) } //insertSheet测试
        println("SHEET INSERT 2")
        sheet2.toEntity()?.let { sheetDao.insertSheet(it) } //insertSheet测试
        println("SHEET GET")
        println(sheetDao.getSheet(1)) //getSheet测试
        println("SHEET GETBYNAME")
        println(sheetDao.getSheetByName("默认清单列表测试_byMirack")) //getSheetByName测试
        println("SHEET GETALL")
        println(sheetDao.getSheets()) //getSheets测试
        /* delete 功能经测试可用，但会违反整个数据库的外键约束，因此此处不作测试 */

        //insert数据
        val task1 = Task(
            name = "向右滑动删除",
            description = null,
            state = TaskState.DOING,
            sheetId = 1,
            tag = null,
            startDate = 123,
            endDate = 123,
            rank = "重要紧急"
        )
        task1.toEntity()?.let { taskDao.insertTask(it) }
        val task2 = Task(
            name = "点击右边图标编辑待办",
            description = null,
            state = TaskState.DOING,
            sheetId = 1,
            tag = null,
            startDate = 123,
            endDate = 123,
            rank = "重要紧急"
        )
        task2.toEntity()?.let { taskDao.insertTask(it) }
        val task3 = Task(
            name = "点击左边图标标记待办为已完成",
            description = null,
            state = TaskState.DOING,
            sheetId = 1,
            tag = null,
            startDate = 123,
            endDate = 123,
            rank = "重要紧急"
        )
        task3.toEntity()?.let { taskDao.insertTask(it) }
        println("TASK GET")
        println(taskDao.getTask(1)) //getTask测试
        println("TASK GETALL")
        println(taskDao.getTasks()) //getTasks测试
        /* 其余功能等待后续实现 */
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