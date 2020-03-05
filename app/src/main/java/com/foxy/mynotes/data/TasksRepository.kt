package com.foxy.mynotes.data

import com.foxy.mynotes.utils.AppExecutors
import com.foxy.mynotes.data.dao.SubTaskDao
import com.foxy.mynotes.data.dao.TaskDao
import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task

class TasksRepository constructor(
    private val executors: AppExecutors,
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao
) {

    fun insertTask(task: Task, subTasks: List<SubTask>) {
        executors.discIO.execute {
            taskDao.insertTask(task)
            subTaskDao.insertSubtasks(subTasks)
        }
    }

    fun insertSubtask(subTask: SubTask) {
        executors.discIO.execute { subTaskDao.insertSubtask(subTask) }
    }

    fun insertSubtasks(subTasks: List<SubTask>) {
        executors.discIO.execute { subTaskDao.insertSubtasks(subTasks) }
    }

    fun updateTask(task: Task, subTasks: List<SubTask>) {
        executors.discIO.execute {
            taskDao.updateTask(task)
            subTaskDao.deleteByTaskId(task.id)
            subTaskDao.insertSubtasks(subTasks)
        }
    }

    fun updateArchived(id: String, archived: Boolean) {
        executors.discIO.execute { taskDao.updateArchived(id, archived) }
    }

    fun updateTaskCompleted(taskId: String, completed: Boolean) {
        executors.discIO.execute {
            taskDao.updateCompleted(taskId, completed)
            subTaskDao.updateTaskCompleted(taskId, completed)
        }
    }

    fun updateSubtaskCompleted(subtaskId: String, completed: Boolean) {
        executors.discIO.execute {
            subTaskDao.updateCompleted(subtaskId, completed)
        }
    }

    fun deleteTask(task: Task) {
        executors.discIO.execute {
            subTaskDao.deleteByTaskId(task.id)
            taskDao.deleteTask(task)
        }
    }

    fun deleteAllTasks() {
        executors.discIO.execute {
            taskDao.deleteAllTasks()
            subTaskDao.deleteAllSubTasks()
        }
    }

    fun deleteSubTask(subTask: SubTask) {
        executors.discIO.execute { subTaskDao.deleteSubTask(subTask) }
    }

    fun getTasks(archived: Boolean, callback: ITasksRepository.LoadTasksCallback) {
        executors.discIO.execute {
            val tasks = taskDao.getTasks(archived)

            executors.mainThread.execute {
                if (tasks.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onDataLoaded(tasks)
                }
            }
        }
    }

    fun getTask(id: String, callback: ITasksRepository.LoadTaskCallback) {
        executors.discIO.execute {
            val task = taskDao.getTask(id)

            executors.mainThread.execute {
                if (task == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onDataLoaded(task)
                }
            }
        }
    }

    fun getSubtasks(taskId: String, callback: ITasksRepository.LoadSubTasksCallback) {
        executors.discIO.execute {
            val subTasks = subTaskDao.getSubTasks(taskId)

            executors.mainThread.execute {
                if (subTasks.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onDataLoaded(subTasks)
                }
            }
        }
    }
}