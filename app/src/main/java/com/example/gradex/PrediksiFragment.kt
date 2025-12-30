package com.example.gradex

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.nio.FloatBuffer

class PrediksiFragment : Fragment(R.layout.fragment_prediksi) {

    private var ortSession: OrtSession? = null
    private val ortEnv = OrtEnvironment.getEnvironment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load Model ONNX
        val modelBytes = resources.assets.open("student_exam.onnx").readBytes()
        ortSession = ortEnv.createSession(modelBytes)

        val spinnerMapel = view.findViewById<Spinner>(R.id.spinnerMapel)
        val listMapel = arrayOf(
            "Bahasa Indonesia",
            "Matematika",
            "Bahasa Inggris",
            "Pendidikan Pancasila",
            "PPKn",
            "Sejarah Indonesia",
            "Seni Budaya",
            "PJOK",
            "Informatika",
            "Bahasa Daerah",
            "Prakarya dan Kewirausahaan",
            "IPA",
            "Fisika",
            "Kimia",
            "Biologi",
            "IPS",
            "Geografi",
            "Ekonomi",
            "Sosiologi"
        )

        spinnerMapel.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listMapel)

        val etBelajar = view.findViewById<EditText>(R.id.belajarInput)
        val etTidur = view.findViewById<EditText>(R.id.tidurInput)
        val etKehadiran = view.findViewById<EditText>(R.id.kehadiranInput)
        val etNilai = view.findViewById<EditText>(R.id.nilaiInput)
        val btnPrediksi = view.findViewById<Button>(R.id.btnPrediksi)

        btnPrediksi.setOnClickListener {
            if (etBelajar.text.isEmpty() || etTidur.text.isEmpty() || etKehadiran.text.isEmpty() || etNilai.text.isEmpty()) {
                Toast.makeText(context, "Isi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Jalankan Inference ML
            val inputArr = floatArrayOf(
                etBelajar.text.toString().toFloat(),
                etTidur.text.toString().toFloat(),
                etKehadiran.text.toString().toFloat(),
                etNilai.text.toString().toFloat()
            )

            val buffer = FloatBuffer.allocate(4).apply { put(inputArr); rewind() }
            val tensor = OnnxTensor.createTensor(ortEnv, buffer, longArrayOf(1, 4))
            val result = ortSession?.run(mapOf("input_data" to tensor))
            val output = (result?.get(0)?.value as Array<FloatArray>)[0][0]

            // Kirim Data ke ResultFragment
            val bundle = Bundle().apply {
                putFloat("prediksi", output)
                putString("MAPEL", spinnerMapel.selectedItem.toString())
                putInt("jamBelajar", etBelajar.text.toString().toInt())
                putInt("jamTidur", etTidur.text.toString().toInt())
                putInt("kehadiran", etKehadiran.text.toString().toInt())
                putInt("nilaiSebelumnya", etNilai.text.toString().toInt())
            }

            val resultFrag = ResultFragment()
            resultFrag.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, resultFrag)
                .addToBackStack(null)
                .commit()
        }
    }
}