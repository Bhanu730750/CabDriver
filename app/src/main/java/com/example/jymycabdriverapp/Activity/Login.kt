package com.example.jymycabdriverapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.jymycabdriverapp.MainActivity
import com.example.jymycabdriverapp.R

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var bttn_login: Button;
        lateinit var txt_signup: TextView;
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bttn_login = findViewById(R.id.bttn_login)
        txt_signup = findViewById(R.id.txt_signup)
        bttn_login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
        txt_signup.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        })
    }
}