package com.example.jymycabdriverapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.jymycabdriverapp.R

class ProfileActivity : AppCompatActivity() {
    lateinit var txt_edit :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        txt_edit = findViewById(R.id.txt_edit)
        txt_edit.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }
    }
}