package com.example.taqui.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taqui.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        auth = FirebaseAuth.getInstance()
        emailInput = findViewById(R.id.email_input)

        val recoverButton = findViewById<Button>(R.id.btn_forgot_password)
        recoverButton.setOnClickListener {
            val email = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                emailInput.error = "O e-mail é obrigatório"
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "E-mail de recuperação enviado", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, RecoverPasswordSuccessActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Falha ao enviar e-mail de recuperação", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
