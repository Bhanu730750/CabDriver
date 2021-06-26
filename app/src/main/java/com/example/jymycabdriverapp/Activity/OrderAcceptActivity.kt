package com.example.jymycabdriverapp.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jymycabdriverapp.MainActivity
import com.example.jymycabdriverapp.R
import com.google.android.material.textfield.TextInputLayout

class OrderAcceptActivity : AppCompatActivity() {
//    lateinit var textinput_otp:TextInputLayout
//    lateinit var bttn_login:Button
    lateinit var ll_accept:LinearLayout
 //   lateinit var layout_otp:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_accept)
//        textinput_otp = findViewById(R.id.textinput_otp)
//        bttn_login = findViewById(R.id.bttn_login)
        ll_accept = findViewById(R.id.ll_accept)
       // layout_otp = findViewById(R.id.layout_otp)
        ll_accept.setOnClickListener {
           // layout_otp.visibility = View.VISIBLE
            val intent = Intent(this, OrderDetail::class.java)
            startActivity(intent)

        }

//        textinput_otp.getEditText()?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//            @SuppressLint("ResourceAsColor")
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                if (s.length < 1) {
//                    bttn_login.setBackgroundResource(R.drawable.backgroundotpedittext)
//                    bttn_login.setTextColor(R.color.light_black1)
//                    bttn_login.isEnabled = false
//
//                }
//                else if (s.length > 0) {
//                    bttn_login.isEnabled = true
//                    bttn_login.setBackgroundResource(R.drawable.button_round_1)
//                    bttn_login.setTextColor(Color.WHITE)
//                }
//            }
//
//            override fun afterTextChanged(s: Editable) {}
//        })
    }
}