package com.foxy.mynotes.data

import com.foxy.mynotes.data.entity.Note

interface INotesRepository {
    interface LoadNotesCallback {
        fun onDataLoaded(notes: MutableList<Note>)
        fun onDataNotAvailable()
    }

    interface LoadNoteCallback {
        fun onDataLoaded(note: Note)
        fun onDataNotAvailable()
    }
}