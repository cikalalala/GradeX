package com.example.gradex

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gradex.database.Tugas
import com.example.gradex.database.Notifikasi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class TugasFragment : Fragment(R.layout.fragment_tugas) {

    private lateinit var dbRef: DatabaseReference
    private val listTugas = mutableListOf<Tugas>()
    private var calendarAlarm: Calendar? = null
    // Tambahkan variabel userId
    private var userId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ambil UID User yang sedang login
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // 2. Sesuaikan Path Database agar masuk ke folder User
        dbRef = FirebaseDatabase.getInstance().getReference("Tugas").child(userId)

        val rv = view.findViewById<RecyclerView>(R.id.rvTugas)
        rv.layoutManager = LinearLayoutManager(context)

        // Listener Real-time (Sekarang membaca dari folder userId)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listTugas.clear()
                for (data in snapshot.children) {
                    val t = data.getValue(Tugas::class.java)
                    if (t != null) listTugas.add(t)
                }
                rv.adapter = TugasAdapter(listTugas) { tugas ->
                    showDialogHapus(tugas)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                if (isAdded) Toast.makeText(context, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })

        view.findViewById<FloatingActionButton>(R.id.fabAddTugas).setOnClickListener {
            showDialogTambah()
        }
    }

    private fun showDialogTambah() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_tambah_tugas, null)

        val spinnerMapel = dialogView.findViewById<Spinner>(R.id.spinnerMapel)
        val etJudul = dialogView.findViewById<EditText>(R.id.etJudulTugas)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDescTugas)
        val tvWaktu = dialogView.findViewById<TextView>(R.id.tvWaktuTerpilih)
        val btnJam = dialogView.findViewById<Button>(R.id.btnSetJam)

        calendarAlarm = null

        // ... (Daftar Mapel Tetap Sama)
        val daftarMapel = listOf("Bahasa Indonesia", "Matematika", "Bahasa Inggris", "Pendidikan Pancasila", "PPKn", "Sejarah Indonesia", "Seni Budaya", "PJOK", "Informatika", "Bahasa Daerah", "Prakarya dan Kewirausahaan", "IPA", "IPS", "Fisika", "Kimia", "Biologi", "Geografi", "Ekonomi", "Sosiologi")
        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, daftarMapel)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMapel.adapter = adapterSpinner

        btnJam.setOnClickListener { showTimePicker(tvWaktu) }

        builder.setView(dialogView)
            .setTitle("Buat Tugas Baru")
            .setPositiveButton("Simpan") { _, _ ->
                val idTugas = dbRef.push().key ?: ""
                val mapelTerpilih = spinnerMapel.selectedItem.toString()
                val judulInput = etJudul.text.toString()

                if (judulInput.isNotEmpty()) {
                    // Menyimpan ke path: Tugas -> userId -> idTugas
                    val tgs = Tugas(idTugas, mapelTerpilih, judulInput, etDesc.text.toString(), false)
                    dbRef.child(idTugas).setValue(tgs).addOnCompleteListener { task ->
                        if (task.isSuccessful && isAdded) {
                            Toast.makeText(context, "Tugas disimpan!", Toast.LENGTH_SHORT).show()
                            scheduleAlarm(idTugas, judulInput)
                            pushNotif("Tugas: $judulInput", "Mapel: $mapelTerpilih")
                        }
                    }
                } else {
                    if (isAdded) Toast.makeText(context, "Judul kosong!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDialogHapus(tugas: Tugas) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Tugas")
            .setMessage("Hapus tugas '${tugas.judul_tugas}'?")
            .setPositiveButton("Hapus") { _, _ ->
                // Menghapus dari path: Tugas -> userId -> idTugas
                dbRef.child(tugas.tugas_id).removeValue().addOnSuccessListener {
                    if (isAdded) {
                        Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                        cancelAlarm(tugas.tugas_id)
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ... (Fungsi Alarm & Notifikasi Tetap Sama)
    private fun showTimePicker(tvWaktu: TextView) { /* ... */ }
    private fun scheduleAlarm(tugasId: String, judul: String) { /* ... */ }
    private fun cancelAlarm(tugasId: String) { /* ... */ }
    private fun pushNotif(judulNotif: String, pesanNotif: String) {
        // Notifikasi juga sebaiknya disimpan per User jika perlu
        val ref = FirebaseDatabase.getInstance().getReference("Notifikasi").child(userId)
        val id = ref.push().key ?: ""
        val jam = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val n = Notifikasi(id, judulNotif, pesanNotif, jam, "TUGAS")
        ref.child(id).setValue(n)
    }
}