package com.foxy.mynotes.mvp.view

import com.foxy.mynotes.data.entity.Note
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface NoteDetailView : MvpView {

    fun onNoteLoaded(note: Note)

    fun onNotesNotAvailable()

    fun onNoteEdit(id: String)

    fun onNoteArchived()

    fun onNoteUndoArchived()

    fun shareNote(note: Note)

    fun onNoteDeleted()

    fun onNoteUndoDeleted()

    fun openNotesListScreen()
}