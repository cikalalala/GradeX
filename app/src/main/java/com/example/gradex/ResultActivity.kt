package com.example.gradex

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.collections.forEach
import kotlin.collections.indices
import kotlin.text.format

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getFloatExtra("prediksi", 0f)

        val txtScore = findViewById<TextView>(R.id.txtScore)
        val txtMessage = findViewById<TextView>(R.id.txtMessage)

        txtScore.text = String.format("%.1f", score)

        // =============================
        // 1. Pesan Berdasarkan Nilai
        // =============================
        txtMessage.text = when {
            score < 50 -> "⚠ Sangat Rendah! Kamu harus meningkatkan usaha secara signifikan!"
            score < 70 -> "⚠ Perlu Perbaikan. Tingkatkan belajar dan pola keseharian!"
            score <= 100 -> "👍 Cukup Baik! Pertahankan dan tingkatkan lagi!"
            else -> "Nilai tidak valid"
        }

        // =============================
        // 2. Rekomendasi Berdasarkan Nilai
        // =============================
        val rekomendasiList = when {
            score < 50 -> listOf(
                "Perlu jam tambahan belajar 3–4 jam/hari",
                "Perbaiki pola tidur segera",
                "Kurangi distraksi HP saat belajar",
                "Minta bimbingan guru atau tutor"
            )

            score < 70 -> listOf(
                "Tambahkan waktu belajar 1–2 jam/hari",
                "Tingkatkan konsistensi tidur",
                "Catat poin penting setiap pelajaran",
                "Jangan skip tugas dan latihan"
            )

            else -> listOf(
                "Pertahankan ritme belajarmu!",
                "Biasakan review materi setiap malam",
                "Ikuti latihan soal tambahan",
                "Tetap jaga pola tidur yang sehat"
            )
        }

        // =============================
        // 3. Faktor Dummy (optional)
        // =============================
        val faktorNames = listOf(
            "Base Score",
            "Jam Belajar",
            "Kualitas Tidur",
            "Kehadiran"
        )

        val faktorValues = listOf(
            "+100.0",
            "+8",
            "+7",
            "+10"
        )

        val container = findViewById<LinearLayout>(R.id.faktorContainer)

        for (i in faktorNames.indices) {
            val item = container.getChildAt(i)
            val name = item.findViewById<TextView>(R.id.txtFactorName)
            val value = item.findViewById<TextView>(R.id.txtFactorValue)
            name.text = faktorNames[i]
            value.text = faktorValues[i]
        }

        // =============================
        // 4. Menampilkan Rekomendasi
        // =============================
        val rekomContainer = findViewById<LinearLayout>(R.id.listRekomendasi)
        rekomContainer.removeAllViews()

        rekomendasiList.forEach { text ->
            val tv = TextView(this)
            tv.text = "✔ $text"
            tv.setTextColor(resources.getColor(R.color.text_dark))
            tv.textSize = 14f
            tv.setPadding(0, 10, 0, 10)
            rekomContainer.addView(tv)
        }
    }
}
