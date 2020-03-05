package com.foxy.mynotes.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foxy.mynotes.R
import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task

class SubTasksAdapter(
    subTasks: List<SubTask>, private val callback: ItemClickListener.SubTaskClick
) : RecyclerView.Adapter<SubTasksAdapter.SubTaskHolder>() {

    var subTasks: List<SubTask> = subTasks
        set(subTasks) {
            field = subTasks
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subtask, parent, false)
        return SubTaskHolder(view)
    }

    override fun getItemCount(): Int = subTasks.size

    override fun onBindViewHolder(holder: SubTaskHolder, position: Int) {
        val subTask = subTasks[position]
        holder.completed.isChecked = subTask.isChecked
        holder.title.text = subTask.title

        holder.itemView.setOnClickListener { callback.onItemClick(subTask) }
        holder.completed.setOnClickListener { callback.onItemClick(subTask) }
        holder.itemView.setOnLongClickListener {
            callback.onItemLongClick(subTask)
            true
        }
    }


    class SubTaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var completed: CheckBox = itemView.findViewById(R.id.checkbox) as CheckBox
        var title: TextView = itemView.findViewById(R.id.tv_title) as TextView
    }
}