package com.example.taqui.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taqui.R
import com.example.taqui.ui.main.AuthenticatedActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: TextInputEditText
    private lateinit var senhaInput: TextInputEditText
    private lateinit var keepConnectedCheckBox: MaterialCheckBox
    private lateinit var loginButton: MaterialButton
    private lateinit var registerButton: MaterialButton
    private lateinit var forgotPasswordButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailInput = findViewById(R.id.email_input)
        senhaInput = findViewById(R.id.senha_input)
        keepConnectedCheckBox = findViewById(R.id.keep_connected)
        loginButton = findViewById(R.id.btn_login)
        registerButton = findViewById(R.id.btn_register)
        forgotPasswordButton = findViewById(R.id.forgot_password)

        loginButton.setOnClickListener {
            handleLogin()
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, RecoverPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleLogin() {
        val email = emailInput.text.toString().trim()
        val senha = senhaInput.text.toString().trim()

        if (email.isEmpty()) {
            emailInput.error = "O e-mail é obrigatório"
            return
        }
        if (senha.isEmpty()) {
            senhaInput.error = "A senha é obrigatória"
            return
        }

        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveSession(email)
                    val intent = Intent(this, AuthenticatedActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveSession(email: String) {
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_email", email)
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }
}
