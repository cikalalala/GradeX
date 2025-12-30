package com.example.gradex

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val userName = intent.getStringExtra("USER_NAME") ?: "User"

        if (savedInstanceState == null) {
            val homeFrag = HomeFragment()
            val bundle = Bundle()
            bundle.putString("USER_NAME", userName)
            homeFrag.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, homeFrag)
                .commit()
        }


        findViewById<ImageView>(R.id.nav_home).setOnClickListener {
            replaceFragment(HomeFragment())
        }

        findViewById<ImageView>(R.id.nav_chart).setOnClickListener {
            // Ganti dengan Fragment yang sesuai nanti
            // replaceFragment(ChartFragment())
        }

        findViewById<ImageView>(R.id.nav_ai).setOnClickListener {
            // replaceFragment(NotificationFragment())
        }

        findViewById<ImageView>(R.id.nav_profile).setOnClickListener {
            // replaceFragment(ProfileFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val bundle = Bundle()
        bundle.putString("USER_NAME", userName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Biar halus
            .commit()
    }
}