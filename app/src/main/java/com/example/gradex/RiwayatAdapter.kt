package com.example.gradex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gradex.database.RiwayatPrediksi

class RiwayatAdapter(private val list: List<RiwayatPrediksi>) :
    RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMapel: TextView = view.findViewById(R.id.txtMapelRiwayat)
        val txtTanggal: TextView = view.findViewById(R.id.txtTanggalRiwayat)
        val btnView: TextView = view.findViewById(R.id.btnViewRiwayat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.txtMapel.text = "Prediksi Nilai ${item.mapel}"
        holder.txtTanggal.text = item.tanggal_riwayat

        holder.btnView.setOnClickListener {
            val fragment = ResultFragment()
            val bundle = Bundle().apply {
                putFloat("prediksi", item.skor.toFloat())
                putString("MAPEL", item.mapel)
                putInt("jamBelajar", item.jamBelajar)
                putInt("jamTidur", item.jamTidur)
                putInt("kehadiran", item.kehadiran)
                putInt("nilaiSebelumnya", item.nilaiSebelumnya)
                putBoolean("isFromHistory", true)
            }
            fragment.arguments = bundle

            val activity = holder.itemView.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount() = list.size
}