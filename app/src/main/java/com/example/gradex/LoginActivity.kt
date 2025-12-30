package com.example.gradex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gradex.database.User
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    // Deklarasi View
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignupTab: Button
    private lateinit var btnLoginTab: Button
    private lateinit var cbRemember: CheckBox
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi  Database
        dbRef = FirebaseDatabase.getInstance().getReference("user")

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignupTab = findViewById(R.id.btnSignupTab)
        btnLoginTab = findViewById(R.id.btnLoginTab)
        cbRemember = findViewById(R.id.cbRemember)


        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email atau password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        btnSignupTab.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        Toast.makeText(this, "Sedang memverifikasi...", Toast.LENGTH_SHORT).show()

        dbRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnap in snapshot.children) {
                            val user = userSnap.getValue(User::class.java)

                            if (user != null && user.password == password) {
                                // Login sukses!
                                val intent = Intent(this@LoginActivity, HomeActivity::class.java)

                                // Kirim firstName saja untuk sapaan di FragmentHome
                                intent.putExtra("USER_NAME", user.firstName)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Password Salah", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Email tidak terdaftar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}