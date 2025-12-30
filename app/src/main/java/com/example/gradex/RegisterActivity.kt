package com.example.gradex

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gradex.database.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.ImageView

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi Database
        dbRef = FirebaseDatabase.getInstance().getReference("user")

        // Inisialisasi View sesuai ID di layout
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // logika Klik Tombol Register
        btnRegister.setOnClickListener {
            val fName = etFirstName.text.toString().trim()
            val lName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Tolong lengkapi semua kolom!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // membuat ID otomatis di database
            // .push() membuat node unik di Firebase, .key mengambil string ID-nya
            val userId = dbRef.push().key ?: ""

            // Buat objek User dengan data lengkap
            val user = User(userId, fName, lName, email, phone, pass)

            // Simpan ke database di bawah folder ID otomatis tadi
            dbRef.child(userId).setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    finish() // Kembali ke LoginActivity
                }
                .addOnFailureListener { err ->
                    Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Kembali ke Login jika teks "Log In" diklik
        tvLoginLink.setOnClickListener {
            finish()
        }
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Menutup halaman register dan balik ke login
        }
    }
}