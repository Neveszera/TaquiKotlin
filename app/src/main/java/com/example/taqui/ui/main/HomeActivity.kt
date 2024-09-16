package com.example.taqui.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.taqui.R
import com.example.taqui.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var greetingTextView: TextView
    private lateinit var usernameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        greetingTextView = findViewById(R.id.greeting_text)
        usernameTextView = findViewById(R.id.username_text)

        val logoutButton = findViewById<Button>(R.id.btn_logout)

        val currentHour = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
        val greeting = when {
            currentHour in 5..11 -> "bom dia,"
            currentHour in 12..17 -> "boa tarde,"
            else -> "boa noite,"
        }
        greetingTextView.text = greeting

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username")
                        usernameTextView.text = username ?: "Usuário"
                    }
                }
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
