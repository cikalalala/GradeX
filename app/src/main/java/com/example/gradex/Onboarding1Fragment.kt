package com.example.gradex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

class Onboarding1Fragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding1, container, false)

        // Logika tombol lanjut ke slide ke-2
        view.findViewById<Button>(R.id.btnNext).setOnClickListener {
            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPagerOnboarding)
            viewPager?.currentItem = 1
        }
        return view
    }
}