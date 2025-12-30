package com.example.gradex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.gradex.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditProfilFragment : Fragment(R.layout.fragment_edit_profile) {

    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("user")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etNewName)
        val etPassword = view.findViewById<EditText>(R.id.etNewPassword)
        val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            // 1. Ambil Nama Lama dari Database
            dbRef.child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(User::class.java)
                    if (userData != null) {
                        etName.setText(userData.firstName)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newPass = etPassword.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userAuth = auth.currentUser
            if (userAuth != null) {
                btnSave.isEnabled = false

                // 2. Update Nama di Realtime Database
                val updateData = HashMap<String, Any>()
                updateData["firstName"] = newName
                // Jika password diisi, update juga field password di database (opsional)
                if (newPass.isNotEmpty()) {
                    updateData["password"] = newPass
                }

                dbRef.child(userAuth.uid).updateChildren(updateData)
                    .addOnSuccessListener {
                        // 3. Cek apakah user ingin ganti Password
                        if (newPass.isNotEmpty()) {
                            if (newPass.length < 6) {
                                Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                                btnSave.isEnabled = true
                                return@addOnSuccessListener
                            }

                            // Update Password di Firebase Authentication
                            userAuth.updatePassword(newPass).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Password diperbarui. Silakan login kembali.", Toast.LENGTH_LONG).show()
                                    logoutUser() // Otomatis keluar
                                } else {
                                    btnSave.isEnabled = true
                                    Toast.makeText(context, "Gagal ganti password: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            // Jika hanya ganti nama
                            Toast.makeText(context, "Nama berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        }
                    }
                    .addOnFailureListener { e ->
                        btnSave.isEnabled = true
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun logoutUser() {
        auth.signOut() // Keluar dari Firebase Auth
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}