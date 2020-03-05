package com.foxy.mynotes.data.dao

import androidx.room.*
import com.foxy.mynotes.data.entity.Note

@Dao interface NoteDao {

    @Insert fun insertNote(note: Note)

    @Update fun updateNote(note: Note)

    @Delete fun deleteNote(note: Note)

    @Query("DELETE FROM notes") fun deleteAllNotes()

    @Query("SELECT * FROM notes WHERE archived = :archived")
    fun getNotes(archived: Boolean): MutableList<Note>

    @Query("SELECT * FROM notes WHERE id = :id") fun getNote(id: String): Note?

    @Query("UPDATE notes SET archived = :archived WHERE id =:id")
    fun updateArchived(id: String, archived: Boolean)
}