package com.foxy.mynotes.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foxy.mynotes.R
import com.foxy.mynotes.data.entity.Task

class TasksAdapter(
    tasks: List<Task>, private val callback: ItemClickListener.TaskClick
) : RecyclerView.Adapter<TasksAdapter.TasksHolder>() {

    var tasks: List<Task> = tasks
        set(tasks) {
            field = tasks
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TasksHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TasksHolder, position: Int) {
        val task = tasks[position]
        holder.completed.isChecked = task.isChecked
        holder.title.text = task.title
        holder.dateOfCreation.text = holder.itemView.resources.getString(
            R.string.format_created_at, task.date.getFormatDateOfCreation()
        )
        holder.dateOfEdit.text = holder.itemView.resources.getString(
            R.string.format_edited_at, task.date.getFormatDateOfEdit()
        )

        holder.itemView.setOnClickListener { callback.onItemClick(task) }
        holder.itemView.setOnLongClickListener {
            callback.onItemLongClick(task)
            true
        }
        holder.completed.setOnClickListener { callback.onChekboxClick(task, holder.completed.isChecked) }
    }


    class TasksHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var completed: CheckBox = itemView.findViewById(R.id.checkbox) as CheckBox
        var title: TextView = itemView.findViewById(R.id.tv_title) as TextView
        var dateOfCreation: TextView = itemView.findViewById(R.id.tv_date_of_creation) as TextView
        var dateOfEdit: TextView = itemView.findViewById(R.id.tv_date_of_edit) as TextView
    }
}