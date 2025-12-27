package com.example.gradex

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.example.gradex.database.User

class HomeActivity : AppCompatActivity() {

    private lateinit var txtGreeting: TextView
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        txtGreeting = findViewById(R.id.txtGreeting)
        dbRef = FirebaseDatabase.getInstance().getReference("users")

        val userName = intent.getStringExtra("user_name") ?: ""

        if (userName.isNotEmpty()) {
            dbRef.orderByChild("name").equalTo(userName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnap in snapshot.children) {
                                val user = userSnap.getValue(User::class.java)
                                if (user != null) {
                                    txtGreeting.text = "Hello, ${user.name}!"
                                }
                            }
                        } else {
                            txtGreeting.text = "Hello!"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        txtGreeting.text = "Hello!"
                    }
                })
        } else {
            txtGreeting.text = "Hello!"
        }
    }
}
