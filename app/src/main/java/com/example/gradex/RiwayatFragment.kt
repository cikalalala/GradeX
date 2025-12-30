package com.example.gradex

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gradex.database.RiwayatPrediksi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RiwayatFragment : Fragment(R.layout.fragment_riwayat) {

    private lateinit var rvRiwayat: RecyclerView
    private val listRiwayat = mutableListOf<RiwayatPrediksi>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRiwayat = view.findViewById(R.id.rvRiwayat)
        rvRiwayat.layoutManager = LinearLayoutManager(context)

        fetchRiwayatData()
    }

    private fun fetchRiwayatData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(context, "Sesi login tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val dbRef = FirebaseDatabase.getInstance().getReference("riwayat_prediksi").child(userId)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return
                listRiwayat.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(RiwayatPrediksi::class.java)
                    if (item != null) listRiwayat.add(item)
                }
                listRiwayat.reverse()
                rvRiwayat.adapter = RiwayatAdapter(listRiwayat)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}