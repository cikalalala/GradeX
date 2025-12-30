package com.example.gradex.database

data class Tugas(
    val tugas_id: String = "",
    val mapel_id: String = "",
    val judul_tugas: String = "",
    val deskripsi: String = "",
    val is_finished: Boolean = false
)