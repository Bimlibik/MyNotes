package com.foxy.mynotes.di

import android.app.Application
import com.foxy.mynotes.MyNotesApp
import com.foxy.mynotes.mvp.presenter.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(modules = [AndroidInjectionModule::class, RepositoryModule::class])
@Singleton
interface AppComponent : AndroidInjector<MyNotesApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
    override fun inject(app: MyNotesApp)

    fun inject(pagesPresenter: PagesPresenter)

    fun inject(notesListPresenter: NotesListPresenter)

    fun inject(notePresenter: NotePresenter)

    fun inject(noteDetailPresenter: NoteDetailPresenter)

    fun inject(tasksListPresenter: TasksListPresenter)

    fun inject(taskPresenter: TaskPresenter)

    fun inject(taskDetailPresenter: TaskDetailPresenter)
}