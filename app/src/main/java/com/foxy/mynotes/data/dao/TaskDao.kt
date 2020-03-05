package com.foxy.mynotes.data.dao

import androidx.room.*
import com.foxy.mynotes.data.entity.Task

@Dao interface TaskDao {

    @Insert
    fun insertTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

    @Query("DELETE FROM tasks") fun deleteAllTasks()

    @Query("SELECT * FROM tasks WHERE archived = :archived")
    fun getTasks(archived: Boolean): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :id") fun getTask(id: String): Task?

    @Query("UPDATE tasks SET archived = :archived WHERE id =:id")
    fun updateArchived(id: String, archived: Boolean)

    @Query("UPDATE tasks SET completed = :completed WHERE id =:id")
    fun updateCompleted(id: String, completed: Boolean)
}