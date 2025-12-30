package com.example.gradex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gradex.database.Tugas
import com.google.firebase.database.FirebaseDatabase

class TugasAdapter(
    private val listTugas: List<Tugas>,
    private val onDeleteClick: (Tugas) -> Unit // Callback untuk fitur hapus
) : RecyclerView.Adapter<TugasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val judul: TextView = view.findViewById(R.id.tvJudulTugas)
        val mapel: TextView = view.findViewById(R.id.tvMapelTugas)
        val cbTugas: CheckBox = view.findViewById(R.id.cbTugas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tugas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listTugas[position]
        holder.judul.text = data.judul_tugas
        holder.mapel.text = data.mapel_id

        // 1. Reset listener ke null agar tidak memicu update otomatis saat binding
        holder.cbTugas.setOnCheckedChangeListener(null)

        // 2. Set status berdasarkan data dari model
        holder.cbTugas.isChecked = data.is_finished

        // 3. Simpan perubahan Checkbox ke Firebase
        holder.cbTugas.setOnClickListener {
            val isChecked = (it as CheckBox).isChecked
            FirebaseDatabase.getInstance().getReference("Tugas")
                .child(data.tugas_id)
                .child("is_finished")
                .setValue(isChecked)
                .addOnFailureListener {
                    holder.cbTugas.isChecked = !isChecked
                }
        }

        // 4. Fitur Hapus via Long Click
        holder.itemView.setOnLongClickListener {
            onDeleteClick(data)
            true
        }
    }

    override fun getItemCount(): Int = listTugas.size
}