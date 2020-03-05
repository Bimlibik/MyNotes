package com.foxy.mynotes.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes")
data class Note (
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var description: String = "",
    @Embedded var date: NoteAndTaskDate = NoteAndTaskDate(),
    var archived: Boolean = false
) {
    val isEmpty get() = title.isEmpty() && description.isEmpty()
}