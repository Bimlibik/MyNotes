package com.foxy.mynotes.mvp.presenter

import android.view.Menu
import com.foxy.mynotes.MyNotesApp
import com.foxy.mynotes.R
import com.foxy.mynotes.data.INotesRepository
import com.foxy.mynotes.data.NotesRepository
import com.foxy.mynotes.data.entity.Note
import com.foxy.mynotes.mvp.view.NoteDetailView
import com.foxy.mynotes.utils.getArchiveStatus
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class NoteDetailPresenter(private val id: String) : MvpPresenter<NoteDetailView>() {

    @Inject
    lateinit var noteRepository: NotesRepository
    lateinit var note: Note

    init {
        MyNotesApp.get().injector.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadNote()
    }

    fun editNote() {
        viewState.onNoteEdit(id)
    }

    fun returnToList() {
        viewState.openNotesListScreen()
    }

    fun moveToArchive() {
        noteRepository.updateArchived(note.id, true)
        viewState.onNoteArchived()
    }

    fun moveFromArchive() {
        noteRepository.updateArchived(note.id, false)
        viewState.onNoteUndoArchived()
    }

    fun shareNote() {
        viewState.shareNote(note)
    }

    fun deleteNote() {
        noteRepository.deleteNote(note)
        viewState.onNoteDeleted()
    }

    fun undoDeleteNote() {
        noteRepository.insertNote(note)
        viewState.onNoteUndoDeleted()
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

    private fun loadNote() {
        noteRepository.getNote(id, object : INotesRepository.LoadNoteCallback {
            override fun onDataLoaded(note: Note) {
                this@NoteDetailPresenter.note = note
                viewState.onNoteLoaded(note)
            }

            override fun onDataNotAvailable() {
                viewState.onNotesNotAvailable()
                returnToList()
            }

        })
    }
}