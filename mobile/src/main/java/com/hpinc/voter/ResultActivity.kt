package com.hpinc.voter

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.View
import android.view.View.OnClickListener


class ResultActivity : Activity() {

    private var button: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        button = findViewById<View>(R.id.button1) as Button

        button!!.setOnClickListener {
            val i = Intent(applicationContext, LoginActivity::class.java)

            startActivity(i)
            overridePendingTransition(R.anim.open_scale, R.anim.close_translate)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_result, menu)
        return true
    }

    override fun onBackPressed() {
        Log.d("CDA", "onBackPressed Called")
        val setIntent = Intent(Intent.ACTION_MAIN)
        setIntent.addCategory(Intent.CATEGORY_HOME)
        setIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(setIntent)
    }
}
