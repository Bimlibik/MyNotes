package com.foxy.mynotes.mvp.presenter

import com.foxy.mynotes.MyNotesApp
import com.foxy.mynotes.R
import com.foxy.mynotes.data.ITasksRepository
import com.foxy.mynotes.data.TasksRepository
import com.foxy.mynotes.data.entity.NoteAndTaskDate
import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task
import com.foxy.mynotes.mvp.view.TaskView
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class TaskPresenter(private val id: String) : MvpPresenter<TaskView>() {

    @Inject
    lateinit var tasksRepository: TasksRepository
    private lateinit var task: Task
    private var subtasks = mutableListOf<SubTask>()
    private var isNew: Boolean = true

    init {
        MyNotesApp.get().injector.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadTask()
    }

    fun saveTask(title: String, date: NoteAndTaskDate, defaultTitle: String) {
        task.title = title
        task.date.dateOfEdit = date.dateOfEdit

        when {
            task.isEmpty && subtasks.isEmpty() -> {
                viewState.openTasksListScreen()
                return
            }
            task.isEmpty -> task.title = defaultTitle
        }

        if (isNew) {
            tasksRepository.insertTask(task, subtasks)
        } else {
            tasksRepository.updateTask(task, subtasks)
        }

        openTaskDetail()
    }

    fun saveSubtask(title: String) {
        if (title.isEmpty()) {
            // Можно показать снекбар - "Введите название задачи"
            return
        }
        val subtask = SubTask(title = title, taskId = id)
        subtasks.add(subtask)
        viewState.onSubtasksLoaded(subtasks)
    }

    fun goBack() {
        if (isNew) {
            openTasksList()
        } else {
            openTaskDetail()
        }
    }

    private fun openTaskDetail() {
        viewState.onTaskSaved(id)
    }

    private fun openTasksList() {
        viewState.openTasksListScreen()
    }

    private fun loadTask() {
        tasksRepository.getTask(id, object : ITasksRepository.LoadTaskCallback {
            override fun onDataLoaded(loadedTask: Task) {
                loadSubtask()
                task = loadedTask
                isNew = false

                viewState.setupToolbarTitle(R.string.toolbar_edit_task)
                viewState.onTaskLoaded(task)
            }

            // Данный callback получим, если задачи нет в БД = новая задача
            override fun onDataNotAvailable() {
                task = if (id.isEmpty()) {
                    Task()
                } else {
                    Task(id = id)
                }
                isNew = true

                viewState.setupToolbarTitle(R.string.toolbar_new_task)
                viewState.onTaskNotAvailable()
            }

        })
    }

    private fun loadSubtask() {
        tasksRepository.getSubtasks(id, object : ITasksRepository.LoadSubTasksCallback {
            override fun onDataLoaded(loadedSubTasks: List<SubTask>) {
                subtasks.clear()
                subtasks.addAll(loadedSubTasks)
                viewState.onSubtasksLoaded(subtasks)
            }

            override fun onDataNotAvailable() {
                // Список подзадач пуст
            }

        })

    }


}