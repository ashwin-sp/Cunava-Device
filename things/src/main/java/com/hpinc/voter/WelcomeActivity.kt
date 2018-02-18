package com.hpinc.voter

import android.os.Bundle
import android.content.Intent
import android.app.Activity
import android.view.Menu
import android.widget.ProgressBar

import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : Activity(), Runnable {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        progressBar.visibility = ProgressBar.VISIBLE
        progressBar.progress = PROGRESS_INITIAL
        progressBar.max = PROGRESS_MAX
        Thread(this).start()
    }

    override fun run() {
        var currentPosition = 0
        while (currentPosition < PROGRESS_MAX) {
            try {
                Thread.sleep(1000)
                currentPosition += PROGRESS_STEP
            } catch (e: InterruptedException) {
                return
            } catch (e: Exception) {
                return
            }

            progressBar.progress = currentPosition
        }
        val from = Intent(this, LoginActivity::class.java)
        startActivity(from)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_welcome, menu)
        return true
    }

    companion object {
        private val PROGRESS_INITIAL = 0
        private val PROGRESS_MAX = 100
        private val PROGRESS_STEP = 10
    }

}
