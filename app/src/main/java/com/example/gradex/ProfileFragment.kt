package com.example.gradex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.gradex.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("user")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtName = view.findViewById<TextView>(R.id.txtProfileName)
        val txtInitial = view.findViewById<TextView>(R.id.txtInitial)
        val btnEdit = view.findViewById<ImageView>(R.id.btnEditProfile)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Ambil data User dari Database berdasarkan UID yang sedang login
            dbRef.child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(User::class.java)
                    if (userData != null) {
                        // Mengambil firstName dari database
                        val nameToShow = userData.firstName ?: "User"

                        txtName.text = nameToShow
                        txtInitial.text = nameToShow.take(1).uppercase()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Jika gagal, tampilkan pesan error singkat
                    Toast.makeText(context, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Pindah ke Halaman Edit
        btnEdit.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, EditProfilFragment())
                .addToBackStack(null)
                .commit()
        }

        // Logout dengan membersihkan task activity
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}