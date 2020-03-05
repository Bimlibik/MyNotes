package com.foxy.mynotes.mvp.view

import com.foxy.mynotes.data.entity.Note
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface NotesListView : MvpView {

    fun onNotesLoaded(notes: List<Note>)

    fun onNotesNotAvailable(emptyText: Int)

    fun updateView(notes: List<Note>)

    fun onAllNotesDeleted()

    fun onNoteDeleted()

    fun onNoteArchived()

    fun onNoteUndoArchived()

    fun openEditNoteScreen(id: String)

    fun openNoteDetails(id: String)

    fun showBottomDialog(note: Note)

    fun hideBottomDialog()

    fun showFilteringMenu()

    fun shareNote(note: Note)

    fun showFabButton()
}