package com.example.timemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import com.example.timemanager.databinding.ActivityAddTodoBinding
import com.example.timemanager.databinding.ActivityMainBinding

class AddTodoActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        // 隐藏系统自带的标题栏
        var actionbar: ActionBar? = supportActionBar
        actionbar?.hide()

        // AddTodo按钮的监听器
        val back = findViewById<Button>(R.id.add_todo_back)
        back.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        finish()
    }
}