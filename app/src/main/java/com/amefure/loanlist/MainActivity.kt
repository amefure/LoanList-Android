package com.amefure.loanlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amefure.loanlist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        binding.floatingActionButton.setOnClickListener {
//            supportFragmentManager.beginTransaction().apply {
//                add(R.id.main_frame,InputFragment())
//                addToBackStack(null)
//                commit()
//            }
//        }
    }
}