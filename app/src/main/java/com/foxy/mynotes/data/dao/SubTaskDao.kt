package com.foxy.mynotes.data.dao

import androidx.room.*
import com.foxy.mynotes.data.entity.SubTask

@Dao interface SubTaskDao {

    @Insert
    fun insertSubtask(subTask: SubTask)

    @Insert
    fun insertSubtasks(subTasks: List<SubTask>)

    @Update
    fun updateSubTask(subTask: SubTask)

    @Update
    fun updateSubTasks(subTask: List<SubTask>)

    @Delete
    fun deleteSubTask(subTask: SubTask)

    @Query("DELETE FROM subTasks WHERE taskId =:taskId") fun deleteByTaskId(taskId: String)

    @Query("DELETE FROM subTasks") fun deleteAllSubTasks()

    @Query("SELECT * FROM subTasks WHERE taskId = :taskId")
    fun getSubTasks(taskId: String): List<SubTask>

    @Query("UPDATE subTasks SET completed = :completed WHERE id =:id")
    fun updateCompleted(id: String, completed: Boolean)

    @Query("UPDATE subTasks SET completed = :completed WHERE taskId =:taskId")
    fun updateTaskCompleted(taskId: String, completed: Boolean)
}