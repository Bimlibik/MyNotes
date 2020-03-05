package com.foxy.mynotes.di

import android.app.Application
import androidx.room.Room
import com.foxy.mynotes.data.NotesDatabase
import com.foxy.mynotes.data.NotesRepository
import com.foxy.mynotes.data.TasksRepository
import com.foxy.mynotes.data.dao.NoteDao
import com.foxy.mynotes.data.dao.SubTaskDao
import com.foxy.mynotes.data.dao.TaskDao
import com.foxy.mynotes.utils.AppExecutors
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Application) : NotesDatabase{
        return Room.databaseBuilder(context.applicationContext,
            NotesDatabase::class.java, "Notes.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideNotesRepository(executors: AppExecutors, noteDao: NoteDao) : NotesRepository {
        return NotesRepository(executors, noteDao)
    }

    @Provides
    @Singleton
    fun provideTasksRepository(executors: AppExecutors, taskDao: TaskDao, subTaskDao: SubTaskDao) : TasksRepository {
        return TasksRepository(executors, taskDao, subTaskDao)
    }

    @Provides
    @Singleton
    fun provideNotesDao(database: NotesDatabase) : NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideTasksDao(database: NotesDatabase) : TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideSubTaskDao(database: NotesDatabase) : SubTaskDao {
        return database.subTaskDao()
    }

    @Provides
    @Singleton
    fun provideAppExecutors() : AppExecutors {
        return AppExecutors()
    }
}