package com.foxy.mynotes.mvp.presenter

import android.view.Menu
import com.foxy.mynotes.MyNotesApp
import com.foxy.mynotes.R
import com.foxy.mynotes.data.ITasksRepository
import com.foxy.mynotes.data.TasksRepository
import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task
import com.foxy.mynotes.mvp.view.TaskDetailView
import com.foxy.mynotes.utils.getArchiveStatus
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class TaskDetailPresenter(private val id: String) : MvpPresenter<TaskDetailView>() {

    @Inject
    lateinit var taskRepository: TasksRepository
    lateinit var task: Task
    private var subtasks = mutableListOf<SubTask>()

    init {
        MyNotesApp.get().injector.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadTask()
        loadSubtask()
    }

    fun editTask() {
        viewState.onTaskEdit(id)
    }

    fun moveToArchive() {
        taskRepository.updateArchived(task.id, true)
        viewState.onTaskArchived()
    }

    fun moveFromArchive() {
        taskRepository.updateArchived(task.id, false)
        viewState.onTaskUndoArchived()
    }

    fun shareTask() {

    }

    fun deleteTask() {
        taskRepository.deleteTask(task)
        viewState.onTaskDeleted()
    }

    fun undoDeleteTask() {
        taskRepository.insertTask(task, subtasks)
        viewState.onTaskUndoDeleted()
    }

    fun updateCompletedSubtasks(subTask: SubTask) {
        subTask.isChecked = !subTask.isChecked
        subtasks.forEach {
            if (it.id == subTask.id) it.isChecked = subTask.isChecked
        }

        taskRepository.updateSubtaskCompleted(subTask.id, subTask.isChecked)
        viewState.onSubTaskLoaded(subtasks)

        updateTask()

        // TODO: add animation to checkbox
    }

    fun returnToList() {
        viewState.openTasksListScreen()
    }

    fun updateOverflowMenu(menu: Menu) {
        if (getArchiveStatus()) {
            menu.findItem(R.id.item_archive).isVisible = false
            menu.findItem(R.id.item_return).isVisible = true
        } else {
            menu.findItem(R.id.item_archive).isVisible = true
            menu.findItem(R.id.item_return).isVisible = false
        }
    }

    private fun updateTask() {
        if (subtasks.isEmpty()) return

        var completed = false
        for (subtask in subtasks) {
            if (!subtask.isChecked) {
                completed = false
                break
            } else {
                completed = true
            }
        }
        taskRepository.updateTaskCompleted(task.id, completed)
    }

    private fun loadTask() {
        taskRepository.getTask(id, object : ITasksRepository.LoadTaskCallback {
            override fun onDataLoaded(loadedTask: Task) {
                task = loadedTask
                viewState.onTaskLoaded(task)
            }

            override fun onDataNotAvailable() {
                viewState.onTaskNotAvailable()
                returnToList()
            }

        })
    }

    private fun loadSubtask() {
        taskRepository.getSubtasks(id, object : ITasksRepository.LoadSubTasksCallback {
            override fun onDataLoaded(loadedSubTasks: List<SubTask>) {
                subtasks.clear()
                subtasks.addAll(loadedSubTasks)
                viewState.onSubTaskLoaded(subtasks)
            }

            override fun onDataNotAvailable() {
                // Список подзадач пуст
            }

        })
    }

}