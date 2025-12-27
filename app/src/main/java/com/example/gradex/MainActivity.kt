package com.example.gradex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Langsung arahkan ke Splash
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }
}
