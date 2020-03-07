package com.foxy.mynotes.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foxy.mynotes.R
import com.foxy.mynotes.data.entity.Note
import com.foxy.mynotes.mvp.presenter.NoteDetailPresenter
import com.foxy.mynotes.mvp.view.NoteDetailView
import com.foxy.mynotes.utils.Page
import com.foxy.mynotes.utils.showSnackBar
import com.foxy.mynotes.utils.showSnackBarWithButton
import com.google.android.material.snackbar.Snackbar
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_note_detail.*

class NoteDetailFragment : MvpAppCompatFragment(), NoteDetailView {

    @InjectPresenter
    lateinit var presenter: NoteDetailPresenter

    @ProvidePresenter
    fun providePresenter(): NoteDetailPresenter {
        val args: NoteDetailFragmentArgs by navArgs()
        return NoteDetailPresenter(args.noteId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_note_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        onBackPressed()

        fab_edit.setOnClickListener { presenter.editNote() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_note_detail, menu)
        presenter.updateOverflowMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.item_archive -> {
                presenter.moveToArchive()
                true
            }
            R.id.item_return -> {
                presenter.moveFromArchive()
                true
            }
            R.id.item_share -> {
                presenter.shareNote()
                true
            }
            R.id.item_delete -> {
                presenter.deleteNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNoteLoaded(note: Note) {
        tv_title.text = note.title
        tv_description.text = note.description
        tv_date_of_creation.text =
            getString(R.string.format_created_at, note.date.getFormatDateOfCreation())
        tv_date_of_edit.text = getString(R.string.format_edited_at, note.date.getFormatDateOfEdit())
    }

    override fun onNotesNotAvailable() {
        showMessage(getString(R.string.snacbar_note_not_available))
    }

    override fun onNoteEdit(id: String) {
        val action =
            NoteDetailFragmentDirections.actionNoteDetailToAddEditNote(
                id
            )
        findNavController().navigate(action)
    }

    override fun onNoteArchived() {
        showMessage(
            getString(R.string.snackbar_note_archived),
            getString(R.string.snackbar_action_undo),
            View.OnClickListener { presenter.moveFromArchive() }
        )
    }

    override fun onNoteUndoArchived() {
        showMessage(getString(R.string.snackbar_note_undo_archived))
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

    override fun onNoteDeleted() {
        showMessage(
            getString(R.string.snackbar_note_deleted),
            getString(R.string.snackbar_action_undo),
            View.OnClickListener { presenter.undoDeleteNote() }
        )
    }

    override fun onNoteUndoDeleted() {
        showMessage(getString(R.string.snacbar_undo_deleted))
    }

    override fun openNotesListScreen() {
        val action = PagesContainerFragmentDirections.actionGlobalMainScreen(Page.NOTES)
        findNavController().navigate(action)
    }

    private fun showMessage(msg: String, btnText: String, listener: View.OnClickListener) {
        view?.showSnackBarWithButton(msg, btnText, listener)
    }

    private fun showMessage(msg: String) {
        view?.showSnackBar(msg, Snackbar.LENGTH_LONG)
    }

    private fun setupToolbar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            presenter.returnToList()
        }
    }
}