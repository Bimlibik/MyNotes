package com.foxy.mynotes.mvp.presenter

import com.foxy.mynotes.MyNotesApp
import com.foxy.mynotes.R
import com.foxy.mynotes.mvp.view.PagesView
import com.foxy.mynotes.utils.getArchiveStatus
import com.foxy.mynotes.utils.setArchiveStatus
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class PagesPresenter : MvpPresenter<PagesView>() {

    private var isArchive = false

    init {
        MyNotesApp.get().injector.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        updateArchiveStatus()
    }

    fun openOrCloseArchive() {
        isArchive = !isArchive
        setArchiveStatus(isArchive)

        viewState.openMainScreen()
    }

    fun updateItemTitle() : Int {
        return if (isArchive) {
            R.string.menu_title_return
        } else {
            R.string.menu_title_archive
        }
    }

    private fun updateView() {
        if (isArchive) {
            viewState.onArchiveLoaded()
        } else {
            viewState.onArchiveClose()
        }
    }

    private fun updateArchiveStatus() {
        val archiveStatus = getArchiveStatus()
        if(archiveStatus.toString().isEmpty()) {
            setArchiveStatus(isArchive)
        } else {
            isArchive = archiveStatus
        }
        updateView()
    }
}