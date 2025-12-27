package com.example.gradex

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import ai.onnxruntime.*
import com.example.gradex.database.Prediksi
import com.example.gradex.database.SemesterUtil
import com.google.firebase.database.FirebaseDatabase
import java.nio.FloatBuffer

class PrediksiActivity : AppCompatActivity() {

    private lateinit var ortEnv: OrtEnvironment
    private lateinit var session: OrtSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediksi)

        ortEnv = OrtEnvironment.getEnvironment()
        val modelBytes = assets.open("student_exam.onnx").readBytes()
        session = ortEnv.createSession(modelBytes)

        val nilai = findViewById<EditText>(R.id.nilaiInput)
        val belajar = findViewById<EditText>(R.id.belajarInput)
        val tidur = findViewById<EditText>(R.id.tidurInput)
        val hadir = findViewById<EditText>(R.id.kehadiranInput)
        val btn = findViewById<Button>(R.id.btnPrediksi)

        btn.setOnClickListener {

            if (nilai.text.isEmpty() || belajar.text.isEmpty()
                || tidur.text.isEmpty() || hadir.text.isEmpty()
            ) {
                Toast.makeText(this, "Isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hoursStudied = belajar.text.toString().toInt()
            val sleepHours = tidur.text.toString().toInt()
            val attendance = hadir.text.toString().toInt()
            val previousScore = nilai.text.toString().toInt()

            // === ML ===
            val buffer = FloatBuffer.allocate(4)
            buffer.put(
                floatArrayOf(
                    hoursStudied.toFloat(),
                    sleepHours.toFloat(),
                    attendance.toFloat(),
                    previousScore.toFloat()
                )
            )
            buffer.rewind()

            val tensor = OnnxTensor.createTensor(
                ortEnv,
                buffer,
                longArrayOf(1, 4)
            )

            val result = session.run(mapOf("input_data" to tensor))
            val predictedScore = (result[0].value as Array<FloatArray>)[0][0].toInt()

            // === DATABASE ===
            val userId = "dummy_user" // nanti ganti dari auth
            val mapelId = "mapel_001"

            val semesterKe = SemesterUtil.getSemesterKe()
            val semesterKey = SemesterUtil.getSemesterKey()

            val database = FirebaseDatabase.getInstance().reference
            val prediksiId = database.child("prediksi").push().key!!

            val data = Prediksi(
                prediksi_id = prediksiId,
                mapel_id = mapelId,
                hours_studied = hoursStudied,
                sleep_hours = sleepHours,
                attendance_percent = attendance,
                previous_score = previousScore,
                predicted_score = predictedScore,
                semester_ke = semesterKe,
                semester_key = semesterKey
            )

            database
                .child("prediksi")
                .child(userId)
                .child(semesterKey)
                .child(prediksiId)
                .setValue(data)

            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("prediksi", predictedScore)
            startActivity(intent)
        }
    }
}
