package com.foxy.mynotes.mvp.presenter

import com.foxy.mynotes.MyNotesApp
import com.foxy.mynotes.R
import com.foxy.mynotes.data.INotesRepository
import com.foxy.mynotes.data.NotesRepository
import com.foxy.mynotes.data.entity.Note
import com.foxy.mynotes.data.entity.NoteAndTaskDate
import com.foxy.mynotes.mvp.view.NoteView
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class NotePresenter(private val id: String) : MvpPresenter<NoteView>() {

    @Inject
    lateinit var notesRepository: NotesRepository
    private lateinit var note: Note
    private var isNew: Boolean = true

    init {
        MyNotesApp.get().injector.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadNote()
    }

    fun saveNote(title: String, description: String, date: NoteAndTaskDate, defaultTitle: String) {
        note.title = title
        note.description = description
        note.date.dateOfEdit = date.dateOfEdit

        if (note.isEmpty) {
            viewState.openNotesListScreen()
            return
        } else if (note.title.isEmpty()) {
            note.title = defaultTitle
        }

        if (isNew) {
            notesRepository.insertNote(note)
        } else {
            notesRepository.updateNote(note)
        }

        openNoteDetail()
    }

    fun goBack() {
        if (isNew) {
            openNotesList()
        } else {
            openNoteDetail()
        }
    }

    private fun openNoteDetail() {
        viewState.onNoteSaved(id)
    }

    private fun openNotesList() {
        viewState.openNotesListScreen()
    }

    private fun loadNote() {
        notesRepository.getNote(id, object : INotesRepository.LoadNoteCallback {
            override fun onDataLoaded(note: Note) {
                this@NotePresenter.note = note
                isNew = false

                viewState.setupToolbarTitle(R.string.toolbar_edit_note)
                viewState.onNoteLoaded(note)
            }

            // Данный callback получим, если заметки нет в БД = новая заметка
            override fun onDataNotAvailable() {
                note = if (id.isEmpty()) {
                    Note()
                } else {
                    Note(id = id)
                }
                isNew = true

                viewState.setupToolbarTitle(R.string.toolbar_new_note)
                viewState.onNoteNotAvailable()
            }

        })
    }
}