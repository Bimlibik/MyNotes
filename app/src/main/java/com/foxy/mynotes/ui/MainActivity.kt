package com.foxy.mynotes.ui

import android.os.Bundle
import com.foxy.mynotes.R
import com.foxy.mynotes.utils.setArchiveStatus
import moxy.MvpAppCompatActivity

class MainActivity : MvpAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        setArchiveStatus(false)
    }
}
