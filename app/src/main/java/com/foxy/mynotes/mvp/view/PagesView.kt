package com.foxy.mynotes.mvp.view
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface PagesView : MvpView {

    fun onArchiveLoaded()

    fun onArchiveClose()

    fun openMainScreen()
}