package com.foxy.mynotes.mvp.presenter

import android.view.View
import android.widget.TextView
import com.foxy.mynotes.MyNotesApp
import com.foxy.mynotes.R
import com.foxy.mynotes.data.INotesRepository
import com.foxy.mynotes.data.NotesRepository
import com.foxy.mynotes.data.entity.Note
import com.foxy.mynotes.mvp.view.NotesListView
import com.foxy.mynotes.utils.*
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class NotesListPresenter : MvpPresenter<NotesListView>() {

    @Inject
    lateinit var notesRepository: NotesRepository
    private var notesList = mutableListOf<Note>()
    private var currentSortMethod = SortNotes.BY_NAME

    init {
        MyNotesApp.get().injector.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        currentSortMethod = getSortMethod()
        loadNotes()
    }

    fun openNewNote() {
        val newNote = Note()
        notesList.add(newNote)
        viewState.openEditNoteScreen(newNote.id)
    }

    fun openNote(note: Note) {
        viewState.openNoteDetails(note.id)
    }

    fun showBottomMenu(note: Note) {
        viewState.showBottomDialog(note)
    }

    fun editNote(id: String) {
        viewState.openEditNoteScreen(id)
        viewState.hideBottomDialog()
    }

    fun moveToArchive(note: Note) {
        notesRepository.updateArchived(note.id, true)
        notesList.remove(note)

        viewState.onNoteArchived()
        viewState.hideBottomDialog()

        updateView()
    }

    fun moveFromArchive(note: Note) {
        notesRepository.updateArchived(note.id, false)
        notesList.remove(note)

        viewState.onNoteUndoArchived()
        viewState.hideBottomDialog()

        updateView()
    }

    fun shareNote(note: Note) {
        viewState.shareNote(note)
        viewState.hideBottomDialog()
    }

    fun deleteNote(note: Note) {
        notesRepository.deleteNote(note)
        notesList.remove(note)

        viewState.onNoteDeleted()
        viewState.hideBottomDialog()

        updateView()
    }

    // Для слушателя кебаб-меню
    fun deleteAllNotes() {
        notesRepository.deleteAllNotes()
        notesList.clear()
        viewState.onAllNotesDeleted()
    }

    fun sortNotesBy(sortMethod: SortNotes) {
        notesList.sortWith(sortMethod)
        setNotesSortMethod(sortMethod.toString())
        updateView()
    }

    fun search(query: String) {
        if (query.equals("")) {
            updateView()
        } else {
            val searchResult = notesList.filter { it.title.startsWith(query, ignoreCase = true) }
            updateView(searchResult, R.string.tv_search_is_empty)
        }
    }

    fun setupVisibility(btnArchive: TextView, btnReturn: TextView) {
        if (getArchiveStatus()) {
            btnArchive.visibility = View.GONE
            btnReturn.visibility = View.VISIBLE
        } else {
            btnArchive.visibility = View.VISIBLE
            btnReturn.visibility = View.GONE
        }
    }

    private fun getSortMethod() : SortNotes {
        val currentMethod = getNotesSortMethod()
        return SortNotes.valueOf(currentMethod)
    }

    private fun updateView(notes: List<Note> = notesList, emptyText: Int = R.string.tv_list_is_empty) {
        if (notes.isEmpty()) {
            viewState.onNotesNotAvailable(emptyText)
        } else {
            viewState.onNotesLoaded(notes)
        }

        if (!getArchiveStatus()) {
            viewState.showFabButton()
        }
    }

    private fun loadNotes() {
        notesRepository.getNotes(getArchiveStatus(), object : INotesRepository.LoadNotesCallback {
            override fun onDataLoaded(notes: MutableList<Note>) {
                notesList.clear()
                notesList.addAll(notes)
                notesList.sortWith(currentSortMethod)
                updateView()
            }

            override fun onDataNotAvailable() {
                updateView()
            }

        })
    }

}