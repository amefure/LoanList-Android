package com.amefure.loanlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amefure.loanlist.View.InputFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val  action:FloatingActionButton = findViewById(R.id.floating_action_button)
        action.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, InputFragment())
                addToBackStack(null)
                commit()
            }
        }
    }
}