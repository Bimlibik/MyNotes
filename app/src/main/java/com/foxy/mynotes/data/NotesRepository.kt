package com.foxy.mynotes.data

import com.foxy.mynotes.utils.AppExecutors
import com.foxy.mynotes.data.dao.NoteDao
import com.foxy.mynotes.data.entity.Note

class NotesRepository constructor(private val executors: AppExecutors, private val noteDao: NoteDao) {

    fun insertNote(note: Note) {
        executors.discIO.execute { noteDao.insertNote(note) }
    }

    fun updateNote(note: Note) {
        executors.discIO.execute { noteDao.updateNote(note) }
    }

    fun updateArchived(id: String, archived: Boolean) {
        executors.discIO.execute { noteDao.updateArchived(id, archived) }
    }

    fun deleteNote(note: Note) {
        executors.discIO.execute { noteDao.deleteNote(note) }
    }

    fun deleteAllNotes() {
        executors.discIO.execute { noteDao.deleteAllNotes() }
    }

    fun getNotes(archived: Boolean, callback: INotesRepository.LoadNotesCallback) {
        executors.discIO.execute {
            val notes = noteDao.getNotes(archived)

            executors.mainThread.execute {
                if (notes.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onDataLoaded(notes)
                }
            }
        }
    }

    fun getNote(id: String, callback: INotesRepository.LoadNoteCallback) {
        executors.discIO.execute {
            val note = noteDao.getNote(id)

            executors.mainThread.execute {
                if (note == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onDataLoaded(note)
                }
            }
        }
    }
}