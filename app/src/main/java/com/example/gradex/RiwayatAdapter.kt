package com.example.gradex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gradex.database.RiwayatPrediksi

class RiwayatAdapter(
    private val list: List<RiwayatPrediksi>,
<<<<<<< HEAD
    private val onDeleteClick: (RiwayatPrediksi) -> Unit // Tambahkan callback untuk hapus
=======
    private val onDeleteClick: (RiwayatPrediksi) -> Unit // Callback untuk hapus
>>>>>>> 99914ce15e774fd21055d1d0a766eebe00cbb4fd
) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMapel: TextView = view.findViewById(R.id.txtMapelRiwayat)
        val txtTanggal: TextView = view.findViewById(R.id.txtTanggalRiwayat)
        val btnView: TextView = view.findViewById(R.id.btnViewRiwayat)
<<<<<<< HEAD
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteRiwayat)
=======
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteRiwayat) // Pastikan ID ini ada di item_riwayat.xml
>>>>>>> 99914ce15e774fd21055d1d0a766eebe00cbb4fd
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.txtMapel.text = "Prediksi Nilai ${item.mapel}"
        holder.txtTanggal.text = item.tanggal_riwayat

<<<<<<< HEAD
        // Navigasi VIEW
=======
        // Klik untuk Lihat Detail
>>>>>>> 99914ce15e774fd21055d1d0a766eebe00cbb4fd
        holder.btnView.setOnClickListener {
            val fragment = ResultFragment()
            val bundle = Bundle().apply {
                putFloat("prediksi", item.skor.toFloat())
                putString("MAPEL", item.mapel)
                putBoolean("isFromHistory", true)
            }
            fragment.arguments = bundle

            val activity = holder.itemView.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
        }

<<<<<<< HEAD
        // Listener untuk tombol HAPUS
=======
        // Klik untuk Hapus
>>>>>>> 99914ce15e774fd21055d1d0a766eebe00cbb4fd
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = list.size
}