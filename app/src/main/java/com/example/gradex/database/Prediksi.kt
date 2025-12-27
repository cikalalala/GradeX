package com.example.gradex.database

data class Prediksi(
    val prediksi_id: String = "",
    val mapel_id: String = "",
    val hours_studied: Int = 0,
    val sleep_hours: Int = 0,
    val attendance_percent: Int = 0,
    val previous_score: Int = 0,
    val predicted_score: Int = 0,
    val semester_ke: Int = 0,
    val semester_key: String = "",
    val created_at: Long = System.currentTimeMillis()
)

