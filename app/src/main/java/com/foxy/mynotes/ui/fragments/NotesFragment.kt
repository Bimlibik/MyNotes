package com.foxy.mynotes.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.app.ShareCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foxy.mynotes.R
import com.foxy.mynotes.data.entity.Note
import com.foxy.mynotes.mvp.presenter.NotesListPresenter
import com.foxy.mynotes.mvp.view.NotesListView
import com.foxy.mynotes.ui.adapters.ItemClickListener
import com.foxy.mynotes.ui.adapters.NotesAdapter
import com.foxy.mynotes.utils.SortNotes
import com.foxy.mynotes.utils.getArchiveStatus
import com.foxy.mynotes.utils.showSnackBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_page.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class NotesFragment : MvpAppCompatFragment(),
    NotesListView {

    @InjectPresenter
    lateinit var presenter: NotesListPresenter
    private lateinit var dialog: BottomSheetDialog

    // Слушатель кликов по заметкам в recyclerView
    private var itemListener: ItemClickListener.NoteClick = object : ItemClickListener.NoteClick {
        override fun onItemClick(note: Note) {
            presenter.openNote(note)
        }

        override fun onItemLongClick(note: Note) {
            presenter.showBottomMenu(note)
        }
    }

    private val notesAdapter: NotesAdapter = NotesAdapter(ArrayList(0), itemListener)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notesAdapter
        }

        fab_add_new.setOnClickListener { presenter.openNewNote() }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        setupSearchView(menu.findItem(R.id.item_search))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_filter -> {
                showFilteringMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNotesLoaded(notes: List<Note>) {
        tv_list_is_empty.visibility = View.GONE
        updateView(notes)
    }

    override fun onNotesNotAvailable(emptyText: Int) {
        tv_list_is_empty.text = getText(emptyText)
        tv_list_is_empty.visibility = View.VISIBLE
        updateView(ArrayList(0))
    }

    override fun updateView(notes: List<Note>) {
        notesAdapter.notes = notes
    }

    override fun onAllNotesDeleted() {
        TODO("удаление всех заметок через overflow меню, диалоговое окно для подтверждения")
    }

    override fun onNoteDeleted() {
        showMessage(getString(R.string.snackbar_note_deleted))
    }

    override fun onNoteArchived() {
        showMessage(getString(R.string.snackbar_note_archived))
    }

    override fun onNoteUndoArchived() {
        showMessage(getString(R.string.snackbar_note_undo_archived))
    }

    override fun openNoteDetails(id: String) {
        val action = PagesContainerFragmentDirections.actionNotesToNoteDetails(id)
        findNavController().navigate(action)
    }

    override fun showBottomDialog(note: Note) {
        val view = layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)
        dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(view)
        dialog.show()

        setupBottomMenuButton(view, note)
    }

    override fun hideBottomDialog() {
        dialog.dismiss()
    }

    override fun showFilteringMenu() {
        val activity = activity ?: return
        val context = context ?: return
        PopupMenu(context, activity.findViewById(R.id.item_filter)).apply {
            menuInflater.inflate(R.menu.filter_notes, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.by_date_creation -> presenter.sortNotesBy(SortNotes.BY_DATE_CREATION)
                    R.id.by_date_edit -> presenter.sortNotesBy(SortNotes.BY_DATE_EDIT)
                    R.id.by_alphabetically -> presenter.sortNotesBy(SortNotes.BY_NAME)
                    else -> presenter.sortNotesBy(SortNotes.BY_NAME)
                }
                true
            }
            show()
        }
    }

    override fun shareNote(note: Note) {
        val shareIntent = ShareCompat.IntentBuilder.from(activity)
            .setChooserTitle(getString(R.string.share_note_title))
            .setType("text/plain")
            .setSubject(note.title)
            .setText(note.description)
            .intent
        startActivity(shareIntent)
    }

    override fun showFabButton() {
        fab_add_new.show()

        // TODO: попробовать перенести fab в pagesContainer - для скрытия / отображения при пролистывании
    }

    override fun openEditNoteScreen(id: String) {
        val action = PagesContainerFragmentDirections.actionNotesToAddEditNote(id)
        findNavController().navigate(action)
    }

    private fun setupSearchView(item: MenuItem) {
        val searchView = item.actionView as SearchView
        searchView.queryHint = getString(R.string.search_notes_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                presenter.search(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                presenter.search(query)
                return false
            }
        })

        searchView.setOnCloseListener {
            presenter.search("")
            false
        }
    }

    private fun setupBottomMenuButton(view: View, note: Note) {
        val btnEdit: TextView = view.findViewById(R.id.bottom_edit_note)
        val btnArchived: TextView = view.findViewById(R.id.bottom_archive)
        val btnReturn: TextView = view.findViewById(R.id.bottom_return)
        val btnShare: TextView = view.findViewById(R.id.bottom_share)
        val btnDelete: TextView = view.findViewById(R.id.bottom_delete)

        btnEdit.setOnClickListener { presenter.editNote(note.id) }
        btnArchived.setOnClickListener { presenter.moveToArchive(note) }
        btnReturn.setOnClickListener { presenter.moveFromArchive(note) }
        btnShare.setOnClickListener { presenter.shareNote(note) }
        btnDelete.setOnClickListener { presenter.deleteNote(note) }

        presenter.setupVisibility(btnArchived, btnReturn)
    }

    private fun showMessage(msg: String) {
        view?.showSnackBar(msg, Snackbar.LENGTH_LONG)
    }
}