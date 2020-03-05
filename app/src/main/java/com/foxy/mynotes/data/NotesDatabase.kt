package com.foxy.mynotes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.foxy.mynotes.data.dao.NoteDao
import com.foxy.mynotes.data.dao.SubTaskDao
import com.foxy.mynotes.data.dao.TaskDao
import com.foxy.mynotes.data.entity.Note
import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task

@Database(entities = [Note::class, Task::class, SubTask::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao() : NoteDao
    abstract fun taskDao() : TaskDao
    abstract fun subTaskDao() : SubTaskDao
}