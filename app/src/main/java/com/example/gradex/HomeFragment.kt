package com.example.gradex

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gradex.database.RiwayatPrediksi
import com.example.gradex.database.User
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var txtGreeting: TextView
    private lateinit var barChart: BarChart
    private lateinit var dbRef: DatabaseReference
    private lateinit var historyRef: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi View & Firebase
        txtGreeting = view.findViewById(R.id.txtGreeting)
        barChart = view.findViewById(R.id.barChart)

        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: ""

        // Path Database
        dbRef = FirebaseDatabase.getInstance().getReference("user")
        historyRef = FirebaseDatabase.getInstance().getReference("riwayat_prediksi").child(userId)

        // 2. Ambil Nama User Berdasarkan UID (Fix Hi User)
        if (userId.isNotEmpty()) {
            dbRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null && !user.firstName.isNullOrEmpty()) {
                        txtGreeting.text = "Hi, ${user.firstName}!!"
                    } else {
                        txtGreeting.text = "Hi, User!!"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    txtGreeting.text = "Hi!!"
                }
            })
        }

        // 3. Setup Grafik
        setupChartAppearance()
        loadChartData()

        // 4. Navigasi Menu
        setupMenuNavigation(view)
    }

    private fun setupChartAppearance() {
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setScaleEnabled(false)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
    }

    private fun loadChartData() {
        historyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mapelScores = mutableMapOf<String, Float>()
                for (item in snapshot.children) {
                    val riwayat = item.getValue(RiwayatPrediksi::class.java)
                    if (riwayat != null) {
                        mapelScores[riwayat.mapel] = riwayat.skor.toFloat()
                    }
                }
                if (mapelScores.isNotEmpty()) {
                    updateChartUI(mapelScores)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateChartUI(scores: Map<String, Float>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val colors = ArrayList<Int>()
        var index = 0f

        // Warna berbeda tiap Bar
        val colorTemplate = listOf(
            android.graphics.Color.parseColor("#1A73E8"), // Biru (Primary)
            android.graphics.Color.parseColor("#4285F4"), // Biru muda
            android.graphics.Color.parseColor("#7E57C2"), // Ungu kebiruan
            android.graphics.Color.parseColor("#9C27B0"), // Ungu
            android.graphics.Color.parseColor("#D81B60"), // Pink tua
            android.graphics.Color.parseColor("#EC407A"), // Pink
            android.graphics.Color.parseColor("#E53935")  // Merah
        )


        scores.forEach { (mapel, skor) ->
            entries.add(BarEntry(index, skor))
            labels.add(mapel)
            colors.add(colorTemplate[index.toInt() % colorTemplate.size])
            index++
        }

        val dataSet = BarDataSet(entries, "Skor Prediksi")
        dataSet.colors = colors
        dataSet.valueTextColor = android.graphics.Color.BLACK
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.labelCount = labels.size
        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun setupMenuNavigation(view: View) {
        view.findViewById<LinearLayout>(R.id.menuCatatan).setOnClickListener { moveFragment(CatatanFragment()) }
        view.findViewById<LinearLayout>(R.id.menuTugas).setOnClickListener { moveFragment(TugasFragment()) }
        view.findViewById<LinearLayout>(R.id.menuPrediksi).setOnClickListener { moveFragment(PrediksiFragment()) }
        view.findViewById<LinearLayout>(R.id.menuRiwayat).setOnClickListener { moveFragment(RiwayatFragment()) }
    }

    private fun moveFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .commit()
    }
}