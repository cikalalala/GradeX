package com.example.gradex

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gradex.database.Notifikasi
import com.google.firebase.database.*

class NotifikasiFragment : Fragment(R.layout.fragment_notifikasi) {

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: NotifikasiAdapter
    private val listNotif = mutableListOf<Notifikasi>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi RecyclerView
        val rv = view.findViewById<RecyclerView>(R.id.rvNotifikasi)
        rv.layoutManager = LinearLayoutManager(context)
        adapter = NotifikasiAdapter(listNotif)
        rv.adapter = adapter

        // 2. Inisialisasi Firebase Reference
        dbRef = FirebaseDatabase.getInstance().getReference("Notifikasi")

        // 3. Ambil data dengan listener tunggal (Data terbaru di atas)
        dbRef.limitToLast(50).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listNotif.clear()
                for (data in snapshot.children) {
                    val n = data.getValue(Notifikasi::class.java)
                    if (n != null) {
                        // Tambahkan ke index 0 agar data terbaru muncul di paling atas
                        listNotif.add(0, n)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) { // Cek jika fragment masih aktif
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}