package com.foxy.mynotes.mvp.view

import com.foxy.mynotes.data.entity.Note
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface NoteView : MvpView {

    fun onNoteLoaded(note: Note)

    fun onNoteNotAvailable()

    fun onNoteSaved(id: String)

    fun setupToolbarTitle(title: Int)

    fun openNotesListScreen()
}