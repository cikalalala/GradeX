package com.example.gradex

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class ArtikelFragment : Fragment(R.layout.fragment_artikel) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Klik Tips Belajar Efektif
        view.findViewById<LinearLayout>(R.id.itemTips1).setOnClickListener {
            cariDiGoogle("tips belajar efektif untuk pelajar")
        }

        // Klik Cara Sukses Ujian
        view.findViewById<LinearLayout>(R.id.itemTips2).setOnClickListener {
            cariDiGoogle("cara sukses ujian nasional dan sekolah")
        }
    }

    private fun cariDiGoogle(keyword: String) {
        val url = "https://www.google.com/search?q=$keyword"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}