package com.example.gradex

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import ai.onnxruntime.*
import java.nio.FloatBuffer

class PrediksiActivity : AppCompatActivity() {

    private lateinit var ortEnv: OrtEnvironment
    private lateinit var session: OrtSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                Toast.makeText(this, "Isi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inputArray = floatArrayOf(
                belajar.text.toString().toFloat(),  // hours_studied
                tidur.text.toString().toFloat(),    // sleep_hours
                hadir.text.toString().toFloat(),    // attendance_percent
                nilai.text.toString().toFloat()     // previous_scores
            )

            val buffer = FloatBuffer.allocate(4)
            buffer.put(inputArray)
            buffer.rewind()

            val tensor = OnnxTensor.createTensor(
                ortEnv,
                buffer,
                longArrayOf(1, 4)
            )

            val result = session.run(mapOf("input_data" to tensor))
            val output = (result[0].value as Array<FloatArray>)[0][0]

            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("prediksi", output)
            startActivity(intent)
        }
    }
}
