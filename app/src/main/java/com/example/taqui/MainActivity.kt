package com.example.taqui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.taqui.ui.main.AuthenticatedActivity
import com.example.taqui.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Redireciona para a AuthenticatedActivity se o usuário está autenticado
        if (currentUser != null) {
            startActivity(Intent(this, AuthenticatedActivity::class.java))
        } else {
            // Caso contrário, redireciona para a LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish() // Encerra MainActivity após redirecionar
    }
}
