package com.example.timemanager.ui.home.adapter

import android.content.Context
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanager.databinding.ItemTaskBinding
import com.google.android.material.card.MaterialCardView
import com.example.timemanager.db.model.Task
import com.example.timemanager.db.model.TaskState

/**
 * [ListAdapter] to show a list of tasks.
 */
class TasksAdapter : ListAdapter<Task, TasksAdapter.ViewHolder>(DIFF_CALLBACK) {
    lateinit var taskClickListener: TaskClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            parent.context
        )
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemTaskBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(task: Task) {
            //binding.task = task
            binding.taskCard.transitionName = task.id.toString()
            if (task.state == TaskState.DOING) {
                binding.checkBox.setOnClickListener {
                    taskClickListener.onTaskDoneClick(task)
                }
                binding.taskNameTextView.text = task.name
                binding.checkBox.isChecked = false
            } else {
                binding.checkBox.setOnClickListener {
                    taskClickListener.onTaskDoingClick(task)
                }
                val spannableString = SpannableString(task.name)
                spannableString.setSpan(
                    StrikethroughSpan(),
                    0,
                    spannableString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.taskNameTextView.text = spannableString
                binding.checkBox.isChecked = true
            }
            binding.editTaskButton.setOnClickListener {
                taskClickListener.onTaskEditClick(task.id, binding.taskCard)
            }
            binding.taskCard.setOnClickListener {
                taskClickListener.onTaskTimingClick(task)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Task>() {

            override fun areItemsTheSame(oldTask: Task, newTask: Task) =
                oldTask.id == newTask.id

            override fun areContentsTheSame(oldTask: Task, newTask: Task) =
                oldTask == newTask
        }
    }

    interface TaskClickListener {
        fun onTaskEditClick(taskId: Int, card: MaterialCardView)
        fun onTaskDoneClick(task: Task)
        fun onTaskDoingClick(task: Task)
        fun onTaskTimingClick(task: Task)
    }
}
