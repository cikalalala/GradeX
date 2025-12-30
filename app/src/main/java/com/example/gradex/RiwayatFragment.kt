package com.example.gradex

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gradex.database.RiwayatPrediksi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RiwayatFragment : Fragment(R.layout.fragment_riwayat) {

    private lateinit var rvRiwayat: RecyclerView
    private val listRiwayat = mutableListOf<RiwayatPrediksi>()
    private lateinit var dbRef: DatabaseReference

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

        dbRef = FirebaseDatabase.getInstance().getReference("riwayat_prediksi").child(userId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return
                listRiwayat.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(RiwayatPrediksi::class.java)
                    if (item != null) listRiwayat.add(item)
                }
                listRiwayat.reverse()

                // Masukkan callback hapus ke adapter
                rvRiwayat.adapter = RiwayatAdapter(listRiwayat) { item ->
                    showDeleteConfirmation(item)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showDeleteConfirmation(item: RiwayatPrediksi) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Riwayat")
            .setMessage("Apakah Anda yakin ingin menghapus riwayat prediksi ${item.mapel}?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteRiwayatItem(item)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteRiwayatItem(item: RiwayatPrediksi) {
        if (item.id.isNotEmpty()) {
            dbRef.child(item.id).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Riwayat berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Gagal menghapus riwayat", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "ID riwayat tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }
}