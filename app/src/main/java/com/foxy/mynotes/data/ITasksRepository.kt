package com.foxy.mynotes.data

import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task

interface ITasksRepository {
    interface LoadTasksCallback {
        fun onDataLoaded(loadedTasks: List<Task>)
        fun onDataNotAvailable()
    }

    interface LoadTaskCallback {
        fun onDataLoaded(loadedTask: Task)
        fun onDataNotAvailable()
    }

    interface LoadSubTasksCallback {
        fun onDataLoaded(loadedSubTasks: List<SubTask>)
        fun onDataNotAvailable()
    }
}