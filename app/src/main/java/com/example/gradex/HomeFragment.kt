package com.example.gradex

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gradex.database.RiwayatPrediksi
import com.example.gradex.database.User
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var txtGreeting: TextView
    private lateinit var imgProfile: ImageView
    private lateinit var barChart: BarChart
    private lateinit var dbRef: DatabaseReference
    private lateinit var historyRef: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View
        txtGreeting = view.findViewById(R.id.txtGreeting)
        imgProfile = view.findViewById(R.id.imgProfileHome)
        barChart = view.findViewById(R.id.barChart)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        dbRef = FirebaseDatabase.getInstance().getReference("user").child(userId)
        historyRef = FirebaseDatabase.getInstance().getReference("riwayat_prediksi").child(userId)

        loadUserProfile()
        setupChartAppearance()
        loadChartData()
        setupMenuNavigation(view)
    }

    private fun loadUserProfile() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    txtGreeting.text = "Hi, ${user.firstName ?: "User"}!!"
                    if (!user.profileImage.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(user.profileImage)
                            .placeholder(R.drawable.ic_user)
                            .into(imgProfile)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
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

        val colorTemplate = listOf("#1A73E8", "#4285F4", "#7E57C2", "#9C27B0", "#E53935")

        scores.forEach { (mapel, skor) ->
            entries.add(BarEntry(index, skor))
            labels.add(mapel)
            colors.add(android.graphics.Color.parseColor(colorTemplate[index.toInt() % colorTemplate.size]))
            index++
        }

        val dataSet = BarDataSet(entries, "Skor Prediksi")
        dataSet.colors = colors
        dataSet.valueTextColor = android.graphics.Color.BLACK

        barChart.apply {
            data = BarData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.labelCount = labels.size
            animateY(1000)
            invalidate()
        }
    }

    private fun setupChartAppearance() {
        barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setScaleEnabled(false)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
            }
        }
    }

    private fun setupMenuNavigation(view: View) {
        view.findViewById<View>(R.id.menuCatatan).setOnClickListener { moveFragment(CatatanFragment()) }
        view.findViewById<View>(R.id.menuTugas).setOnClickListener { moveFragment(TugasFragment()) }
        view.findViewById<View>(R.id.menuPrediksi).setOnClickListener { moveFragment(PrediksiFragment()) }
        view.findViewById<View>(R.id.menuRiwayat).setOnClickListener { moveFragment(RiwayatFragment()) }
    }

    private fun moveFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .commit()
    }
}