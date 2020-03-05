package com.foxy.mynotes.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "subTasks",
    foreignKeys = [
        ForeignKey(entity = Task::class, parentColumns = ["id"], childColumns = ["taskId"])
    ]
)
data class SubTask (
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    @ColumnInfo(name = "completed") var isChecked: Boolean = false,
    var taskId: String = ""
)