package com.foxy.mynotes.ui.adapters

import com.foxy.mynotes.data.entity.Note
import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task

interface ItemClickListener {

    interface NoteClick {
        fun onItemClick(note: Note)
        fun onItemLongClick(note: Note)
    }

    interface TaskClick {
        fun onItemClick(task: Task)
        fun onItemLongClick(task: Task)
        fun onChekboxClick(task: Task, completed: Boolean)
    }

    interface SubTaskClick {
        fun onItemClick(subTask: SubTask)
        fun onItemLongClick(subTask: SubTask)
    }
}