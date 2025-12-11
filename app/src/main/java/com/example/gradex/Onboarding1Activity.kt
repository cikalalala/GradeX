package com.example.gradex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Onboarding1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding1)

        val next = findViewById<Button>(R.id.btnNext)

        next.setOnClickListener {
            startActivity(Intent(this, Onboarding2Activity::class.java))
        }
    }
}
