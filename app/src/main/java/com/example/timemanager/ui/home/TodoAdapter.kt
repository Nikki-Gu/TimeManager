package com.example.timemanager.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanager.R

class TodoAdapter(private val dataSet: ArrayList<TodoItem>) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val editButton: ImageButton
        val checkBox: CheckBox

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.textView)
            editButton = view.findViewById(R.id.edit_button)
            checkBox = view.findViewById(R.id.checkbox)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.todo_item, viewGroup, false)

        val viewHolder = ViewHolder(view)
        viewHolder.editButton.setOnClickListener {
            // editbutton选中事件
            val position = viewHolder.adapterPosition
            val item = dataSet[position]
            Toast.makeText(viewGroup.context, "${item.title} edit event",
                Toast.LENGTH_SHORT).show()
        }
        viewHolder.checkBox.setOnClickListener {
            // checkbox选中事件
            val position = viewHolder.adapterPosition
            val item = dataSet[position]
            item.flag=viewHolder.checkBox.isChecked
            Toast.makeText(viewGroup.context, "${item.title} is ${item.flag}",
                Toast.LENGTH_SHORT).show()
        }
        return viewHolder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].title
        viewHolder.checkBox.isChecked = dataSet[position].flag
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
