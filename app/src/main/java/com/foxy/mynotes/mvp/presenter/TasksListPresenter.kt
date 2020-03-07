package com.foxy.mynotes.mvp.presenter

import android.view.View
import android.widget.TextView
import com.foxy.mynotes.MyNotesApp
import com.foxy.mynotes.R
import com.foxy.mynotes.data.ITasksRepository
import com.foxy.mynotes.data.TasksRepository
import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task
import com.foxy.mynotes.mvp.view.TasksListView
import com.foxy.mynotes.utils.*
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class TasksListPresenter : MvpPresenter<TasksListView>() {

    @Inject
    lateinit var tasksRepository: TasksRepository
    private var tasks = mutableListOf<Task>()
    private var currentSortMethod = SortTasks.ALL

    init {
        MyNotesApp.get().injector.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        currentSortMethod = getSortMethod()
        loadTasks()
    }

    fun openNewTask() {
        val newTask = Task()
        tasks.add(newTask)
        viewState.openEditTaskScreen(newTask.id)
    }

    fun openTask(task: Task) {
        viewState.openTaskDetails(task.id)
    }

    fun updateCompleted(task: Task, completed: Boolean) {
        tasksRepository.updateTaskCompleted(task.id, completed)
        tasksRepository.updateSubtasksCompleted(task.id, completed)
        tasks.forEach {
            if (it.id == task.id) it.isChecked = completed
        }

        sortTask()
    }

    fun showBottomMenu(task: Task) {
        viewState.showBottomDialog(task)
    }

    fun editTask(id: String) {
        viewState.openEditTaskScreen(id)
        viewState.hideBottomDialog()
    }

    fun moveToArchive(task: Task) {
        tasksRepository.updateArchived(task.id, true)
        tasks.remove(task)

        viewState.onTaskArchived()
        viewState.hideBottomDialog()

        updateView()
    }

    fun moveFromArchive(task: Task) {
        tasksRepository.updateArchived(task.id, false)
        tasks.remove(task)

        viewState.onTaskUndoArchived()
        viewState.hideBottomDialog()

        updateView()
    }

    fun shareTask(task: Task) {
        val subtasks = mutableListOf<SubTask>()
        tasksRepository.getSubtasks(task.id, object : ITasksRepository.LoadSubTasksCallback {
            override fun onDataLoaded(loadedSubTasks: List<SubTask>) {
                subtasks.addAll(loadedSubTasks)
            }

            override fun onDataNotAvailable() {
                // subtasks not found
            }

        })
        viewState.shareTask(task, subtasks)
        viewState.hideBottomDialog()
    }

    fun deleteTask(task: Task) {
        tasksRepository.deleteTask(task)
        tasks.remove(task)

        viewState.onTaskDeleted()
        viewState.hideBottomDialog()

        updateView()
    }

    fun sortTask(sortMethod: SortTasks = currentSortMethod) {
        // TODO: add default sort by date of creation

        if (sortMethod != currentSortMethod) {
            currentSortMethod = sortMethod
            setTasksSortMethod(sortMethod.toString())
        }

        val sortedTasks = mutableListOf<Task>()
        for (task in tasks) {
            when (currentSortMethod) {
                SortTasks.ALL -> sortedTasks.add(task)
                SortTasks.ACTIVE -> if (task.isActive) sortedTasks.add(task)
                SortTasks.COMPLETED -> if (task.isChecked) sortedTasks.add(task)
            }
        }

        updateView(sortedTasks)
    }

    fun search(query: String) {
        if (query.equals("")) {
            updateView()
        } else {
            val searchResult = tasks.filter { it.title.startsWith(query, ignoreCase = true) }
            updateView(searchResult, R.string.tv_search_is_empty)
        }
    }

    fun setupVisibility(btnArchive: TextView, btnReturn: TextView) {
        if (getArchiveStatus()) {
            btnArchive.visibility = View.GONE
            btnReturn.visibility = View.VISIBLE
        } else {
            btnArchive.visibility = View.VISIBLE
            btnReturn.visibility = View.GONE
        }
    }

    private fun getSortMethod(): SortTasks {
        val currentMethod = getTasksSortMethod()
        return SortTasks.valueOf(currentMethod)
    }

    private fun updateView(tasksList: List<Task> = tasks, emptyText: Int = R.string.tv_list_is_empty) {
        if (tasksList.isEmpty()) {
            viewState.onTasksNotAvailable(emptyText)
        } else {
            viewState.onTasksLoaded(tasksList)
        }

        if (!getArchiveStatus()) {
            viewState.showFabButton()
        }
    }

    private fun loadTasks() {
        tasksRepository.getTasks(getArchiveStatus(), object : ITasksRepository.LoadTasksCallback {
            override fun onDataLoaded(loadedTasks: List<Task>) {
                tasks.clear()
                tasks.addAll(loadedTasks)
                sortTask()
            }

            override fun onDataNotAvailable() {
                updateView()
            }


        })
    }
}