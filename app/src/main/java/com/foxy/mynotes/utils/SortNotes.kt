package com.foxy.mynotes.utils

import com.foxy.mynotes.data.entity.Note

enum class SortNotes : Comparator<Note> {
    BY_DATE_CREATION {
        override fun compare(note1: Note?, note2: Note?): Int =
            note1!!.date.dateOfCreation.compareTo(note2!!.date.dateOfCreation)
    },

    BY_DATE_EDIT {
        override fun compare(note1: Note?, note2: Note?): Int =
            note1!!.date.dateOfEdit.compareTo(note2!!.date.dateOfEdit)
    },

    BY_NAME {
        override fun compare(note1: Note?, note2: Note?): Int =
            note1!!.title.compareTo(note2!!.title)
    }
}