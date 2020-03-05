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
import com.foxy.mynotes.data.entity.SubTask
import com.foxy.mynotes.data.entity.Task
import com.foxy.mynotes.mvp.presenter.TasksListPresenter
import com.foxy.mynotes.mvp.view.TasksListView
import com.foxy.mynotes.ui.adapters.ItemClickListener
import com.foxy.mynotes.ui.adapters.TasksAdapter
import com.foxy.mynotes.utils.SortTasks
import com.foxy.mynotes.utils.showSnackBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_page.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class TasksFragment : MvpAppCompatFragment(), TasksListView {

    @InjectPresenter
    lateinit var presenter: TasksListPresenter

    //View
    private lateinit var dialog: BottomSheetDialog

    // recyclerView listener
    private var itemListener: ItemClickListener.TaskClick = object : ItemClickListener.TaskClick {
        override fun onItemClick(task: Task) {
            presenter.openTask(task)
        }

        override fun onItemLongClick(task: Task) {
            presenter.showBottomMenu(task)
        }

        override fun onChekboxClick(task: Task, completed: Boolean) {
            presenter.updateCompleted(task, completed)
        }
    }

    private val tasksAdapter: TasksAdapter = TasksAdapter(ArrayList(0), itemListener)


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
            adapter = tasksAdapter
        }

        fab_add_new.setOnClickListener { presenter.openNewTask() }
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

    override fun onTasksLoaded(tasks: List<Task>) {
        tv_list_is_empty.visibility = View.GONE
        updateView(tasks)
    }

    override fun onTasksNotAvailable(emptyText: Int) {
        tv_list_is_empty.text = getText(emptyText)
        tv_list_is_empty.visibility = View.VISIBLE
        updateView(ArrayList(0))
    }

    override fun updateView(tasks: List<Task>) {
        tasksAdapter.tasks = tasks
    }

    override fun onAllTasksDeleted() {
        TODO("подумать над действием" +
                "- удалять вообще все (вместе с архивом)" +
                "- удалять текущий активный список (архив не трогать)" +
                "- удалять завершенные задачи")
    }

    override fun onTaskDeleted() {
        showMessage(getString(R.string.snackbar_task_deleted))
    }

    override fun onTaskArchived() {
        showMessage(getString(R.string.snackbar_task_archived))
    }

    override fun onTaskUndoArchived() {
        showMessage(getString(R.string.snackbar_task_undo_archived))
    }

    override fun shareTask(task: Task, subtasks: List<SubTask>) {
        val shareIntent = ShareCompat.IntentBuilder.from(activity)
            .setChooserTitle(getString(R.string.share_task_title))
            .setType("text/plain")
            .setSubject(task.title)
            .setText(subtasks.toString())
            .intent
        startActivity(shareIntent)
    }

    override fun openEditTaskScreen(id: String) {
        val action =
            PagesContainerFragmentDirections.actionTasksToAddEditTasks(
                id
            )
        findNavController().navigate(action)
    }

    override fun openTaskDetails(id: String) {
        val action =
            PagesContainerFragmentDirections.actionTasksToTaskDetail(
                id
            )
        findNavController().navigate(action)
    }

    override fun showBottomDialog(task: Task) {
        val view = layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)
        dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(view)
        dialog.show()

        setupBottomMenuButton(view, task)
    }

    override fun hideBottomDialog() {
        dialog.dismiss()
    }

    override fun showFilteringMenu() {
        val activity = activity ?: return
        val context = context ?: return
        PopupMenu(context, activity.findViewById(R.id.item_filter)).apply {
            menuInflater.inflate(R.menu.filter_tasks, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.all ->  presenter.sortTask(SortTasks.ALL)
                    R.id.active -> presenter.sortTask(SortTasks.ACTIVE)
                    R.id.complete -> presenter.sortTask(SortTasks.COMPLETED)
                    else -> presenter.sortTask(SortTasks.ALL)
                }
                true
            }
            show()
        }
    }

    override fun showFabButton() {
        fab_add_new.show()

        // TODO: попробовать перенести fab в pagesContainer - для скрытия / отображения при пролистывании
    }

    private fun setupSearchView(item: MenuItem) {
        val searchView = item.actionView as SearchView
        searchView.queryHint = getString(R.string.search_tasks_hint)

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

    private fun setupBottomMenuButton(view: View, task: Task) {
        val btnEdit: TextView = view.findViewById(R.id.bottom_edit_note)
        val btnArchived: TextView = view.findViewById(R.id.bottom_archive)
        val btnReturn: TextView = view.findViewById(R.id.bottom_return)
        val btnShare: TextView = view.findViewById(R.id.bottom_share)
        val btnDelete: TextView = view.findViewById(R.id.bottom_delete)

        btnEdit.setOnClickListener { presenter.editTask(task.id) }
        btnArchived.setOnClickListener { presenter.moveToArchive(task) }
        btnReturn.setOnClickListener { presenter.moveFromArchive(task) }
        btnShare.setOnClickListener { presenter.shareTask(task) }
        btnDelete.setOnClickListener { presenter.deleteTask(task) }

        presenter.setupVisibility(btnArchived, btnReturn)
    }

    private fun showMessage(msg: String) {
        view?.showSnackBar(msg, Snackbar.LENGTH_LONG)
    }
}