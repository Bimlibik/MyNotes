package com.foxy.mynotes.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.foxy.mynotes.R
import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task
import com.foxy.mynotes.mvp.presenter.TaskDetailPresenter
import com.foxy.mynotes.mvp.view.TaskDetailView
import com.foxy.mynotes.ui.adapters.ItemClickListener
import com.foxy.mynotes.ui.adapters.SubTasksAdapter
import com.foxy.mynotes.utils.Page
import com.foxy.mynotes.utils.showSnackBar
import com.foxy.mynotes.utils.showSnackBarWithButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_task_detail.*
import kotlinx.android.synthetic.main.fragment_task_detail.tv_title
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class TaskDetailFragment : MvpAppCompatFragment(), TaskDetailView {

    @InjectPresenter
    lateinit var presenter: TaskDetailPresenter

    @ProvidePresenter
    fun providePresenter(): TaskDetailPresenter {
        val args: TaskDetailFragmentArgs by navArgs()
        return TaskDetailPresenter(args.taskId)
    }

    private var itemListener: ItemClickListener.SubTaskClick = object : ItemClickListener.SubTaskClick {
        override fun onItemClick(subTask: SubTask) {
            presenter.updateCompletedSubtasks(subTask)
        }

        override fun onItemLongClick(subTask: SubTask) {
            // TODO: long click action
        }
    }

    private val subTasksAdapter: SubTasksAdapter = SubTasksAdapter(ArrayList(0), itemListener)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_task_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_subtask.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = subTasksAdapter
        }

        setupToolbar()
        onBackPressed()

        fab_edit.setOnClickListener{ presenter.editTask() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_note_detail, menu)
        presenter.updateOverflowMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.item_archive -> {
                presenter.moveToArchive()
                true
            }
            R.id.item_share -> {
                presenter.shareTask()
                true
            }
            R.id.item_delete -> {
                presenter.deleteTask()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onTaskLoaded(task: Task) {
        tv_title.text = task.title
        tv_date_of_creation.text = getString(R.string.format_created_at, task.date.getFormatDateOfCreation())
        tv_date_of_edit.text = getString(R.string.format_edited_at, task.date.getFormatDateOfEdit())
    }

    override fun onSubTaskLoaded(subTasks: List<SubTask>) {
        subTasksAdapter.subTasks = subTasks
    }

    override fun onTaskNotAvailable() {
        showMessage(getString(R.string.snacbar_task_not_available))
    }

    override fun onTaskEdit(id: String) {
        val action =
            TaskDetailFragmentDirections.actionTaskDetailToAddEditTask(
                id
            )
        findNavController().navigate(action)
    }

    override fun onTaskArchived() {
        showMessage(
            getString(R.string.snackbar_task_archived),
            getString(R.string.snackbar_action_undo),
            View.OnClickListener { presenter.moveFromArchive() }
        )
    }

    override fun onTaskUndoArchived() {
        showMessage(getString(R.string.snackbar_task_undo_archived))
    }

    override fun shareTask(task: Task) {
        TODO("create new share intent")
    }

    override fun onTaskDeleted() {
        showMessage(
            getString(R.string.snackbar_task_deleted),
            getString(R.string.snackbar_action_undo),
            View.OnClickListener { presenter.undoDeleteTask() }
        )
    }

    override fun onTaskUndoDeleted() {
        showMessage(getString(R.string.snacbar_undo_deleted))
    }

    override fun openTasksListScreen() {
        val action = PagesContainerFragmentDirections.actionGlobalMainScreen(Page.TASKS)
        findNavController().navigate(action)

        // TODO: open task tab
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