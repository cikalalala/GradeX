package com.example.gradex.database

data class Tugas(
    val tugas_id: String = "",
    val mapel_id: String = "",
    val judul_tugas: String = "",
    val deskripsi: String = "",
    @field:JvmField // Penting agar Firebase mengenali boolean dengan benar
    var is_finished: Boolean = false
)