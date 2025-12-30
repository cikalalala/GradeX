package com.example.gradex.database

data class RiwayatPrediksi(
    val id: String = "",
    val mapel: String = "",
    val skor: Double = 0.0,
    val tanggal_riwayat: String = "",
    val jamBelajar: Int = 0,
    val jamTidur: Int = 0,
    val kehadiran: Int = 0,
    val nilaiSebelumnya: Int = 0
)