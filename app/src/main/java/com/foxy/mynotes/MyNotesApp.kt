package com.foxy.mynotes

import android.app.Application
import com.foxy.mynotes.di.AppComponent
import com.foxy.mynotes.di.DaggerAppComponent

class MyNotesApp : Application() {
    lateinit var injector: AppComponent
    private set

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        injector = DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    companion object {
        private lateinit var INSTANCE : MyNotesApp

        @JvmStatic
        fun get() : MyNotesApp = INSTANCE
    }
}