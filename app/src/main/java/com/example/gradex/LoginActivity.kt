package com.example.gradex

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gradex.database.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("user")

        // Konfigurasi Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignupTab = findViewById<Button>(R.id.btnSignupTab)
        val btnGoogleLogin = findViewById<LinearLayout>(R.id.btnGoogleLogin)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // 1. Logika Login Email & Password
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Logika Forgot Password
        tvForgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                showForgotPassDialog(email)
            } else {
                // Jika kolom email kosong, tampilkan dialog input
                showForgotPassDialog("")
            }
        }

        // 3. Logika Google Sign-In
        btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        btnSignupTab.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showForgotPassDialog(prefilledEmail: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset Password")
        builder.setMessage("Masukkan email Anda untuk menerima link reset password.")

        val input = EditText(this)
        input.setText(prefilledEmail)
        input.hint = "Email Address"

        // Memberi sedikit padding pada EditText di dalam Dialog
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(50, 20, 50, 20)
        input.layoutParams = params
        container.addView(input)
        builder.setView(container)

        builder.setPositiveButton("Kirim") { _, _ ->
            val email = input.text.toString().trim()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Link reset password telah dikirim ke email", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In Gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                val userId = firebaseUser?.uid ?: ""

                dbRef.child(userId).get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        val newUser = User(
                            userId,
                            firebaseUser?.displayName ?: "Google User",
                            "",
                            firebaseUser?.email ?: "",
                            "",
                            ""
                        )
                        dbRef.child(userId).setValue(newUser)
                    }
                    navigateToHome(firebaseUser?.displayName ?: "User")
                }
            }
        }
    }

    private fun loginUser(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: ""
                dbRef.child(userId).get().addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(User::class.java)
                    navigateToHome(user?.firstName ?: "User")
                }
            } else {
                Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToHome(userName: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("USER_NAME", userName)
        startActivity(intent)
        finish()
    }
}