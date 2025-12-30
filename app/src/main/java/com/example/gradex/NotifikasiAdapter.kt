package com.example.gradex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gradex.database.Notifikasi

class NotifikasiAdapter(private val listNotif: List<Notifikasi>) :
    RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tvJudulNotif)
        val tvPesan: TextView = view.findViewById(R.id.tvPesanNotif)
        val tvWaktu: TextView = view.findViewById(R.id.tvWaktuNotif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_notifikasi, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listNotif[position]
        holder.tvJudul.text = data.notif_judul
        holder.tvPesan.text = data.notif_pesan
        holder.tvWaktu.text = data.notif_waktu
    }

    override fun getItemCount(): Int = listNotif.size
}