package com.example.gradex.database
import java.util.Calendar

object SemesterUtil {

    fun getSemesterKe(): Int {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        return if (month in 1..6) 2 else 1
    }

    fun getSemesterKey(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        return if (month in 1..6) {
            "genap_$year"
        } else {
            "ganjil_$year"
        }
    }
}
