package com.example.gradex

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Navigasi Bottom Bar
        findViewById<ImageView>(R.id.nav_home).setOnClickListener { replaceFragment(HomeFragment()) }
        findViewById<ImageView>(R.id.nav_chart).setOnClickListener { replaceFragment(ArtikelFragment()) }
        findViewById<ImageView>(R.id.nav_ai).setOnClickListener { replaceFragment(NotifikasiFragment()) }
        findViewById<ImageView>(R.id.nav_profile).setOnClickListener { replaceFragment(ProfileFragment()) }
    }

    private fun replaceFragment(fragment: Fragment) {
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val bundle = Bundle()
        bundle.putString("USER_NAME", userName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .commit()
    }
}