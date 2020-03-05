package com.foxy.mynotes.data.entity

import android.text.format.DateFormat
import androidx.room.ColumnInfo

data class NoteAndTaskDate(
    @ColumnInfo(name = "date_of_creation") var dateOfCreation: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "date_of_edit") var dateOfEdit: Long = System.currentTimeMillis()
) {
    fun getFormatDateOfCreation() : String {
        return DateFormat.format("dd.MM.yyyy", dateOfCreation).toString()
    }

    fun getFormatDateOfEdit() : String {
        return DateFormat.format("dd.MM.yyyy", dateOfEdit).toString()
    }
}