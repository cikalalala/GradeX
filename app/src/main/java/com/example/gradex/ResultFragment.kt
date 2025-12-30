package com.example.gradex

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.gradex.database.RiwayatPrediksi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class ResultFragment : Fragment(R.layout.fragment_result) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ambil data dari Fragment sebelumnya (Input) - TETAP SAMA
        val score = arguments?.getFloat("prediksi", 0f) ?: 0f
        val mapel = arguments?.getString("MAPEL") ?: "Umum"
        val jBelajar = arguments?.getInt("jamBelajar", 0) ?: 0
        val jTidur = arguments?.getInt("jamTidur", 0) ?: 0
        val hadir = arguments?.getInt("kehadiran", 0) ?: 0
        val nLama = arguments?.getInt("nilaiSebelumnya", 0) ?: 0
        val isFromHistory = arguments?.getBoolean("isFromHistory", false) ?: false

        val txtScore = view.findViewById<TextView>(R.id.txtScore)
        val txtMessage = view.findViewById<TextView>(R.id.txtMessage)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpanResult)

        val rekomendasiContainer = view.findViewById<LinearLayout>(R.id.listRekomendasi)

        // Menampilkan skor
        txtScore.text = String.format("%.1f", score)

        // Logika Pesan Rekomendasi (Kode Asli Anda)
        val message = when {
            score < 50 -> "⚠ Sangat Rendah! Butuh evaluasi mendalam."
            score < 70 -> "⚠ Perlu Perbaikan! Tingkatkan jam belajar."
            else -> "👍 Cukup Baik! Pertahankan performamu."
        }
        txtMessage.text = message

        // --- MULAI LOGIKA BARU: DAFTAR TIPS DINAMIS ---
        val tips = when {
            score < 50 -> listOf("Fokus pada materi dasar", "Tambah jam belajar +3 jam", "Kurangi gadget malam hari")
            score < 70 -> listOf("Review materi sebelum tidur", "Perbanyak latihan soal", "Jaga konsistensi kehadiran")
            else -> listOf("Pertahankan pola belajar", "Eksplorasi materi tingkat lanjut", "Bantu teman belajar")
        }

        // Tampilkan tips ke dalam container jika container tersedia di XML
        rekomendasiContainer?.removeAllViews()
        tips.forEach { tip ->
            val tv = TextView(requireContext())
            tv.text = "• $tip"
            tv.textSize = 14f
            tv.setPadding(0, 5, 0, 5)
            tv.setTextColor(android.graphics.Color.DKGRAY)
            rekomendasiContainer?.addView(tv)
        }
        // --- AKHIR LOGIKA BARU ---

        // Setup Tampilan Faktor Analisis (Jam Belajar, Tidur, dll) - TETAP SAMA
        val factorContainer = view.findViewById<LinearLayout>(R.id.faktorContainer)
        val fValues = listOf("$jBelajar jam", "$jTidur jam", "$hadir%", "$nLama")
        for (i in 0 until (factorContainer?.childCount ?: 0)) {
            val item = factorContainer.getChildAt(i)
            item?.findViewById<TextView>(R.id.tvNilaiFaktor)?.text = fValues.getOrNull(i) ?: ""
        }

        // 2. Kontrol Tombol Simpan - TETAP SAMA
        if (isFromHistory) {
            btnSimpan?.visibility = View.GONE
        } else {
            btnSimpan?.visibility = View.VISIBLE
            btnSimpan?.setOnClickListener {
                saveToFirebase(mapel, score.toDouble(), jBelajar, jTidur, hadir, nLama)
            }
        }
    }

    // Fungsi saveToFirebase tetap sama seperti kode Anda sebelumnya
    private fun saveToFirebase(mapel: String, score: Double, jB: Int, jT: Int, h: Int, nL: Int) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user == null) {
            Toast.makeText(context, "Gagal: Sesi login tidak ditemukan.", Toast.LENGTH_LONG).show()
            return
        }

        val userId = user.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("riwayat_prediksi").child(userId)
        val id = dbRef.push().key ?: ""
        val tanggal = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val data = RiwayatPrediksi(id, mapel, score, tanggal, jB, jT, h, nL)

        view?.findViewById<Button>(R.id.btnSimpanResult)?.isEnabled = false

        dbRef.child(id).setValue(data).addOnSuccessListener {
            if (isAdded) {
                Toast.makeText(context, "Riwayat Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, RiwayatFragment())
                    .commit()
            }
        }.addOnFailureListener { e ->
            view?.findViewById<Button>(R.id.btnSimpanResult)?.isEnabled = true
            Toast.makeText(context, "Gagal Simpan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}