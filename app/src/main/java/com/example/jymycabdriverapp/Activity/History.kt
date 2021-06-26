package com.example.jymycabdriverapp.Activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.jymycabdriverapp.R
import com.example.jymycabdriverapp.ui.CategoryFragment
import com.google.android.material.tabs.TabLayout

class History : AppCompatActivity() {
    var tabLayout: TabLayout? = null
    var frameLayout: FrameLayout? = null
    var fragment: Fragment? = null
    var fragmentTransaction: FragmentTransaction? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        tabLayout = findViewById(R.id.tabLayout)
        frameLayout = findViewById(R.id.frameLayout)
        tabLayout!!.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffffff"));
        fragment = CategoryFragment()
        // fragmentManager = requireActivity().supportFragmentManager
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction!!.replace(R.id.frameLayout, fragment!!)
        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction!!.commit()
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // creating cases for fragment
                when (tab.position) {
                    0 -> {
                        fragment = CategoryFragment()
                    }
                    1 ->{
                        fragment = CategoryFragment()
                    }
                    2 ->{
                        fragment = CategoryFragment()
                    }
                    3 ->{
                        fragment = CategoryFragment()
                    }

                }
                fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction!!.replace(R.id.frameLayout, fragment!!)
                fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                fragmentTransaction!!.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {


            }

        })


    }
}