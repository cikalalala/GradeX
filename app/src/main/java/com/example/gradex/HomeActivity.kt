package com.example.gradex

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Ambil data USER_NAME dari Intent awal
        val userName = intent.getStringExtra("USER_NAME") ?: "User"

        if (savedInstanceState == null) {
            // Fragment pertama yang muncul adalah Home
            replaceFragment(HomeFragment())
        }

        // 1. Menu Pertama: HOME
        findViewById<ImageView>(R.id.nav_home).setOnClickListener {
            replaceFragment(HomeFragment())
        }

        // 2. Menu Kedua: ARTIKEL (Sebelumnya nav_chart)
        findViewById<ImageView>(R.id.nav_chart).setOnClickListener {
            replaceFragment(ArtikelFragment())
        }

        // 3. Menu Ketiga: NOTIFIKASI (Sebelumnya nav_ai)
        findViewById<ImageView>(R.id.nav_ai).setOnClickListener {
            replaceFragment(NotifikasiFragment())
        }

        // 4. Menu Keempat: PROFIL
        findViewById<ImageView>(R.id.nav_profile).setOnClickListener {
            // Menggunakan ProfilFragment yang kita buat sebelumnya
            replaceFragment(ProfileFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val bundle = Bundle()
        // Terus kirimkan USER_NAME agar Greeting di HomeFragment tetap aktif
        bundle.putString("USER_NAME", userName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            // Tidak menggunakan addToBackStack agar navigasi navbar utama tetap bersih
            .commit()
    }
}