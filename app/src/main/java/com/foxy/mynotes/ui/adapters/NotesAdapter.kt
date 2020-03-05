package com.foxy.mynotes.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foxy.mynotes.R
import com.foxy.mynotes.data.entity.Note

class NotesAdapter(
    notes: List<Note>, private val callback: ItemClickListener.NoteClick
) : RecyclerView.Adapter<NotesAdapter.NotesHolder>() {

    var notes: List<Note> = notes
        set(notes) {
            field = notes
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NotesHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        val note = notes[position]
        holder.title.text = note.title
        holder.dateOfCreation.text = holder.itemView.resources.getString(
            R.string.format_created_at, note.date.getFormatDateOfCreation()
        )
        holder.dateOfEdit.text = holder.itemView.resources.getString(
            R.string.format_edited_at, note.date.getFormatDateOfEdit()
        )

        holder.itemView.setOnClickListener { callback.onItemClick(note) }
        holder.itemView.setOnLongClickListener {
            callback.onItemLongClick(note)
            true
        }
    }


    class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.tv_title) as TextView
        var dateOfCreation: TextView = itemView.findViewById(R.id.tv_date_of_creation) as TextView
        var dateOfEdit: TextView = itemView.findViewById(R.id.tv_date_of_edit) as TextView
    }
}