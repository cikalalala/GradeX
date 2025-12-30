package com.example.gradex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtName = view.findViewById<TextView>(R.id.txtProfileName)
        val txtInitial = view.findViewById<TextView>(R.id.txtInitial)
        val btnEdit = view.findViewById<ImageView>(R.id.btnEditProfile)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Ambil data dari Bundle (yang dikirim HomeActivity)
        val userName = arguments?.getString("USER_NAME") ?: "User"
        txtName.text = userName
        txtInitial.text = userName.take(1).uppercase()

        // Pindah ke Halaman Edit
        btnEdit.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, EditProfilFragment())
                .addToBackStack(null)
                .commit()
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}