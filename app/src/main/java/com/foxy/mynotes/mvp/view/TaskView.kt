package com.foxy.mynotes.mvp.view

import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface TaskView : MvpView {

    fun onTaskLoaded(task: Task)

    fun onSubtasksLoaded(subtasks: List<SubTask>)

    fun onTaskNotAvailable()

    fun onTaskSaved(id: String)

    fun setupToolbarTitle(title: Int)

    fun openTasksListScreen()
}