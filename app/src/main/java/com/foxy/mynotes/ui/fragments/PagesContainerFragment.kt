package com.foxy.mynotes.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.foxy.mynotes.R
import com.foxy.mynotes.mvp.presenter.PagesPresenter
import com.foxy.mynotes.mvp.view.PagesView
import com.foxy.mynotes.ui.adapters.SimplePagerAdapter
import kotlinx.android.synthetic.main.fragment_pages_container.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class PagesContainerFragment : MvpAppCompatFragment(), PagesView {

    @InjectPresenter
    lateinit var presenter: PagesPresenter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_pages_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_pages, menu)
        menu.findItem(R.id.item_archive).title = getString(presenter.updateItemTitle())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.item_settings -> {
                Toast.makeText(context, "settings", Toast.LENGTH_LONG).show()
                true
            }
            R.id.item_archive -> {
                presenter.openOrCloseArchive()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onArchiveLoaded() {
        setupArchiveToolbar()
        // TODO: загружены заметки из архива, можно сменить цвет тулбара / фона
    }

    override fun onArchiveClose() {
        setupToolbar()
        // TODO: загружены стандартные заметки, можно сменить цвет тулбара / фона
    }

    override fun openMainScreen() {
        val action = PagesContainerFragmentDirections.actionGlobalMainScreen()
        findNavController().navigate(action)
    }

    private fun setupViewPager() {
        val tabTitles: Array<String> = resources.getStringArray(R.array.tabs_title)
        val pageAdapter = SimplePagerAdapter(childFragmentManager)

        pageAdapter.addFragment(NotesFragment())
        pageAdapter.addFragment(TasksFragment())
        pageAdapter.addTitles(tabTitles)

        viewpager.adapter = pageAdapter
        tabs.setupWithViewPager(viewpager)
    }

    private fun setupToolbar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }
        toolbar.title = getString(R.string.app_name)
    }

    private fun setupArchiveToolbar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.title = getString(R.string.menu_title_archive)
        onBackPressed()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            presenter.openOrCloseArchive()
        }
    }
}