package com.foxy.mynotes.ui.fragments

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foxy.mynotes.R
import com.foxy.mynotes.data.entity.Note
import com.foxy.mynotes.data.entity.NoteAndTaskDate
import com.foxy.mynotes.mvp.presenter.NotePresenter
import com.foxy.mynotes.mvp.view.NoteView
import com.foxy.mynotes.utils.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_note_add_edit.*
import kotlinx.android.synthetic.main.fragment_note_add_edit.fab_save
import kotlinx.android.synthetic.main.fragment_note_add_edit.field_title
import kotlinx.android.synthetic.main.fragment_note_add_edit.toolbar

class AddEditNoteFragment : MvpAppCompatFragment(), NoteView {

    @InjectPresenter
    lateinit var presenter: NotePresenter

    @ProvidePresenter
    fun providePresenter(): NotePresenter {
        val args: AddEditNoteFragmentArgs by navArgs()
        return NotePresenter(args.noteId)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_note_add_edit, container, false)

        view.registerAnimation(context)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        setupToolbar()
        setupFab()
        setupKeyListener()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNoteLoaded(note: Note) {
        field_title.setText(note.title)
        field_description.setText(note.description)
        field_description.showKeyboard()
        field_description.setSelection(field_description.text.length)
    }

    override fun onNoteNotAvailable() {
        field_title.showKeyboard()
    }

    override fun onNoteSaved(id: String) {
        val action = AddEditNoteFragmentDirections.actionAddEditNoteToNoteDetail(id)
        findNavController().navigate(action)
    }

    override fun setupToolbarTitle(title: Int) {
        toolbar.title = getString(title)
    }

    override fun openNotesListScreen() {
        val action = PagesContainerFragmentDirections.actionGlobalFade(Page.NOTES)
        findNavController().navigate(action)
        registerExitAnimation(context, view!!)
    }

    private fun setupKeyListener() {
        // for API >= 26
        field_title.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    field_description.requestFocus()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupToolbar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupFab() {
        fab_save.setOnClickListener {
            presenter.saveNote(
                title = field_title.text.toString(),
                description = field_description.text.toString(),
                date = NoteAndTaskDate(),
                defaultTitle = getString(R.string.note_title_is_empty)
            )
            it.hideKeyboard()
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            presenter.goBack()
        }
    }
}