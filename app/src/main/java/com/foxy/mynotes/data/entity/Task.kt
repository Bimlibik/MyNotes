package com.foxy.mynotes.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks")
data class Task (
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    @ColumnInfo(name = "completed") var isChecked: Boolean = false,
    var archived: Boolean = false,
    @Embedded var date: NoteAndTaskDate = NoteAndTaskDate()
) {
    val isEmpty get() = title.isEmpty()

    val isActive get() = !isChecked
}