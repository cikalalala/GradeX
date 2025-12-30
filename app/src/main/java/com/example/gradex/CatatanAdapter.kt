package com.example.gradex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gradex.database.Catatan

class CatatanAdapter(
    private val listCatatan: List<Catatan>,
    private val onViewClick: (Catatan) -> Unit // Callback untuk mengirim data ke Fragment
) : RecyclerView.Adapter<CatatanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMapel: TextView = view.findViewById(R.id.tvJudulCatatan)
        val tvIsi: TextView = view.findViewById(R.id.tvIsiCatatan)
        val btnView: TextView = view.findViewById(R.id.btnView) // ID sesuai XML Anda
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_catatan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listCatatan[position]

        // Menampilkan nama mapel dan judul ringkas di list
        holder.tvMapel.text = data.nama_mapel
        holder.tvIsi.text = data.judul

        // Menangani klik tombol VIEW
        holder.btnView.setOnClickListener {
            onViewClick(data)
        }
    }

    override fun getItemCount(): Int = listCatatan.size
}