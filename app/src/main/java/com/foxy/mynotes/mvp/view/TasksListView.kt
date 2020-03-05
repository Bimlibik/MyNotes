package com.foxy.mynotes.mvp.view

import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface TasksListView : MvpView {

    fun onTasksLoaded(tasks: List<Task>)

    fun onTasksNotAvailable(emptyText: Int)

    fun updateView(tasks: List<Task>)

    fun onAllTasksDeleted()

    fun onTaskDeleted()

    fun onTaskArchived()

    fun onTaskUndoArchived()

    fun shareTask(task: Task, subtasks: List<SubTask>)

    fun openEditTaskScreen(id: String)

    fun openTaskDetails(id: String)

    fun showBottomDialog(task: Task)

    fun hideBottomDialog()

    fun showFilteringMenu()

    fun showFabButton()
}