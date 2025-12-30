package com.example.gradex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gradex.database.Catatan
import com.example.gradex.database.Notifikasi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class CatatanFragment : Fragment(R.layout.fragment_catatan) {

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: CatatanAdapter
    private val listCatatan = mutableListOf<Catatan>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referensi ke node "Catatan" di Firebase Realtime Database
        dbRef = FirebaseDatabase.getInstance().getReference("Catatan")

        val rv = view.findViewById<RecyclerView>(R.id.rvCatatan)
        rv.layoutManager = LinearLayoutManager(context)

        // Mendengarkan perubahan data secara real-time
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return

                listCatatan.clear()
                for (data in snapshot.children) {
                    val c = data.getValue(Catatan::class.java)
                    if (c != null) listCatatan.add(c)
                }

                // Inisialisasi adapter dengan callback untuk showDetailDialog
                adapter = CatatanAdapter(listCatatan) { catatanTerpilih ->
                    showDetailDialog(catatanTerpilih)
                }
                rv.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) Toast.makeText(context, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Menangani klik FloatingActionButton untuk tambah catatan
        view.findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showTambahDialog()
        }
    }

    private fun showTambahDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_tambah_catatan, null)

        val spinnerMapel = viewInflated.findViewById<Spinner>(R.id.spinnerMapelCatatan)
        val inputJudul = viewInflated.findViewById<EditText>(R.id.etJudulCatatan)
        val inputIsi = viewInflated.findViewById<EditText>(R.id.etIsiCatatan)

        val daftarMapel = listOf(
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
            "IPS",
            "Fisika",
            "Kimia",
            "Biologi",
            "Geografi",
            "Ekonomi",
            "Sosiologi"
        )

        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, daftarMapel)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMapel.adapter = adapterSpinner

        builder.setView(viewInflated)
            .setTitle("Tambah Catatan Baru")
            .setPositiveButton("Simpan") { _, _ ->
                val mapel = spinnerMapel.selectedItem.toString()
                val judul = inputJudul.text.toString()
                val isi = inputIsi.text.toString()

                if (judul.isNotEmpty() && isi.isNotEmpty()) {
                    simpanCatatan(mapel, judul, isi)
                } else {
                    Toast.makeText(context, "Judul dan Isi wajib diisi!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // Menampilkan detail lengkap catatan saat tombol VIEW diklik
    private fun showDetailDialog(catatan: Catatan) {
        val builder = AlertDialog.Builder(requireContext())
        val viewDetail = LayoutInflater.from(context).inflate(R.layout.dialog_tambah_catatan, null)

        val inputJudul = viewDetail.findViewById<EditText>(R.id.etJudulCatatan)
        val inputIsi = viewDetail.findViewById<EditText>(R.id.etIsiCatatan)
        val spinnerMapel = viewDetail.findViewById<Spinner>(R.id.spinnerMapelCatatan)

        // Isi data
        inputJudul.setText(catatan.judul)
        inputIsi.setText(catatan.isi)

        // --- PENGATURAN SCROLL & READ-ONLY ---
        inputJudul.isEnabled = false // Judul biasanya pendek, cukup disable saja

        // Agar isi tetap bisa di-scroll meskipun tidak bisa diedit:
        inputIsi.isFocusable = false      // Tidak bisa diklik untuk ngetik
        inputIsi.isCursorVisible = false  // Kursor hilang
        inputIsi.keyListener = null       // Tidak bisa input karakter apapun

        // Mengaktifkan Scroll secara manual melalui kode
        inputIsi.setMovementMethod(android.text.method.ScrollingMovementMethod())
        // -------------------------------------

        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf(catatan.nama_mapel))
        spinnerMapel.adapter = adapterSpinner
        spinnerMapel.isEnabled = false

        builder.setView(viewDetail)
            .setTitle("Detail Catatan")
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Hapus") { _, _ ->
                dbRef.child(catatan.catatan_id).removeValue().addOnSuccessListener {
                    if (isAdded) Toast.makeText(context, "Catatan dihapus", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun simpanCatatan(mapel: String, judul: String, isi: String) {
        val id = dbRef.push().key ?: ""
        val tanggal = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        val newNote = Catatan(id, mapel, judul, isi, tanggal)

        dbRef.child(id).setValue(newNote).addOnCompleteListener { task ->
            if (task.isSuccessful && isAdded) {
                Toast.makeText(context, "Catatan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                pushNotif("Catatan Baru", "Menambahkan catatan $judul pada mapel $mapel")
            }
        }
    }

    private fun pushNotif(judulNotif: String, pesanNotif: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Notifikasi")
        val id = ref.push().key ?: ""
        val jam = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val n = Notifikasi(id, judulNotif, pesanNotif, jam, "INFO")
        ref.child(id).setValue(n)
    }
}