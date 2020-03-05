package com.foxy.mynotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.foxy.mynotes.R
import com.foxy.mynotes.data.entity.NoteAndTaskDate
import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task
import com.foxy.mynotes.mvp.presenter.TaskPresenter
import com.foxy.mynotes.mvp.view.TaskView
import com.foxy.mynotes.ui.adapters.ItemClickListener
import com.foxy.mynotes.ui.adapters.SubTasksAdapter
import kotlinx.android.synthetic.main.fragment_task_add_edit.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class AddEditTaskFragment : MvpAppCompatFragment(), TaskView {

    @InjectPresenter
    lateinit var presenter: TaskPresenter

    private var itemListener: ItemClickListener.SubTaskClick = object :ItemClickListener.SubTaskClick {
        override fun onItemClick(subTask: SubTask) {
            TODO("Change checked status")
        }

        override fun onItemLongClick(subTask: SubTask) {
            TODO("Open bottom menu")
        }
    }

    private val subtasksAdapter: SubTasksAdapter = SubTasksAdapter(ArrayList(0), itemListener)

    @ProvidePresenter
    fun providePresenter(): TaskPresenter {
        val args: AddEditTaskFragmentArgs by navArgs()
        return TaskPresenter(args.taskId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_task_add_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_subtask.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = subtasksAdapter
        }

        onBackPressed()
        setupToolbar()
        setupFab()
        setupSubtaskButton()
        setupKeyboardListener()
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

    override fun onTaskLoaded(task: Task) {
        field_title.setText(task.title)
        field_subtask.requestFocus()
    }

    override fun onSubtasksLoaded(subtasks: List<SubTask>) {
        subtasksAdapter.subTasks = subtasks
    }

    override fun onTaskNotAvailable() {
        field_title.requestFocus()
    }

    override fun onTaskSaved(id: String) {
        val action =
            AddEditTaskFragmentDirections.actionAddEditTaskToTaskDetail(
                id
            )
        findNavController().navigate(action)
    }

    override fun setupToolbarTitle(title: Int) {
        toolbar.title = getString(title)
    }

    override fun openTasksListScreen() {
        findNavController().navigate(R.id.action_global_main_screen)
    }

    private fun setupToolbar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupFab() {
        fab_save.setOnClickListener {
            presenter.saveTask(
                title = field_title.text.toString(),
                date = NoteAndTaskDate(),
                defaultTitle = getString(R.string.note_title_is_empty)
            )
        }
    }

    private fun setupKeyboardListener() {
        field_subtask.setOnEditorActionListener { textView, actionId, keyEvent ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    presenter.saveSubtask(field_subtask.text.toString())
                    field_subtask.setText("")
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSubtaskButton() {
        btn_add_subtask.setOnClickListener {
            presenter.saveSubtask(field_subtask.text.toString())
            field_subtask.setText("")
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) { presenter.goBack() }
    }
}