package com.example.gradex

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gradex.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.ImageView

class RegisterActivity : AppCompatActivity() {

    // Deklarasi Firebase Auth dan Database
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("user")

        // 2. Inisialisasi View
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        // Logika Klik Tombol Register
        btnRegister.setOnClickListener {
            val fName = etFirstName.text.toString().trim()
            val lName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            // Validasi Input
            if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Tolong lengkapi semua kolom!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Jalankan proses pendaftaran
            registerUser(fName, lName, email, phone, pass)
        }

        tvLoginLink.setOnClickListener { finish() }
        btnBack.setOnClickListener { finish() }
    }

    private fun registerUser(fName: String, lName: String, email: String, phone: String, pass: String) {
        // Tampilkan pesan loading
        Toast.makeText(this, "Sedang mendaftarkan...", Toast.LENGTH_SHORT).show()

        // 3. DAFTARKAN KE FIREBASE AUTHENTICATION TERLEBIH DAHULU
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Jika sukses, ambil UID (User ID) yang dibuat oleh Firebase Auth
                    val userId = auth.currentUser?.uid ?: ""

                    // 4. SIMPAN DATA LENGKAP KE REALTIME DATABASE MENGGUNAKAN UID TERSEBUT
                    // Ini kunci agar Rules "auth != null" dan "$uid === auth.uid" bekerja
                    val user = User(userId, fName, lName, email, phone, pass)

                    dbRef.child(userId).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()
                            finish() // Kembali ke LoginActivity
                        }
                        .addOnFailureListener { err ->
                            Toast.makeText(this, "Database Error: ${err.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Jika gagal (misal: email sudah terdaftar atau format salah)
                    Toast.makeText(this, "Auth Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}