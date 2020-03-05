package com.foxy.mynotes.mvp.view

import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface TaskDetailView : MvpView {

    fun onTaskLoaded(task: Task)

    fun onSubTaskLoaded(subTasks: List<SubTask>)

    fun onTaskNotAvailable()

    fun onTaskEdit(id: String)

    fun onTaskArchived()

    fun onTaskUndoArchived()

    fun shareTask(task: Task)

    fun onTaskDeleted()

    fun onTaskUndoDeleted()

    fun openTasksListScreen()
}