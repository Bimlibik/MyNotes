package com.foxy.mynotes.utils

import android.content.Context
import com.foxy.mynotes.MyNotesApp

private val prefs by lazy {
    MyNotesApp.get().getSharedPreferences("MyNotesPrefs", Context.MODE_PRIVATE)
}

private const val NOTES_SORT_METHOD = "notes_sort_method"
private const val TASKS_SORT_METHOD = "tasks_sort_method"
private const val IS_ARCHIVE = "is_archive"

fun getNotesSortMethod() : String =
    prefs.getString(NOTES_SORT_METHOD, SortNotes.BY_NAME.toString())!!

fun setNotesSortMethod(sortMethod: String) {
    prefs.edit().putString(NOTES_SORT_METHOD, sortMethod).apply()
}

fun getTasksSortMethod() : String =
    prefs.getString(TASKS_SORT_METHOD, SortTasks.ALL.toString())!!

fun setTasksSortMethod(sortMethod: String) {
    prefs.edit().putString(TASKS_SORT_METHOD, sortMethod).apply()
}

fun getArchiveStatus() : Boolean =
    prefs.getBoolean(IS_ARCHIVE, false)

fun setArchiveStatus(isArchive: Boolean) {
    prefs.edit().putBoolean(IS_ARCHIVE, isArchive).apply()
}