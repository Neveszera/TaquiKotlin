package com.example.taqui.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taqui.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern
import android.content.Intent


class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val usernameInput = findViewById<TextInputEditText>(R.id.username_input)
        val emailInput = findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password_input)
        val passwordConfirmInput = findViewById<TextInputEditText>(R.id.password_confirm_input)
        val cpfInput = findViewById<TextInputEditText>(R.id.cpf_input)
        val signupButton = findViewById<Button>(R.id.btn_signup)

        signupButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = passwordConfirmInput.text.toString().trim()
            val cpf = cpfInput.text.toString().trim()

            if (validateInput(username, email, password, confirmPassword, cpf)) {
                createUser(email, password, username, cpf)
            }
        }
    }

    private fun validateInput(username: String, email: String, password: String, confirmPassword: String, cpf: String): Boolean {
        return when {
            username.isEmpty() -> {
                showToast("Por favor, insira um nome de usuário")
                false
            }
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Por favor, insira um e-mail válido")
                false
            }
            password.isEmpty() || password.length < 6 -> {
                showToast("A senha deve ter pelo menos 6 caracteres")
                false
            }
            confirmPassword != password -> {
                showToast("As senhas não coincidem")
                false
            }
            cpf.isEmpty() || !Pattern.matches("\\d{11}", cpf) -> {
                showToast("Por favor, insira um CPF válido")
                false
            }
            else -> true
        }
    }

    private fun createUser(email: String, password: String, username: String, cpf: String) {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val formattedCpf = cpf.trim()

        firestore.collection("users")
            .whereEqualTo("cpf", formattedCpf)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                val userMap = hashMapOf(
                                    "username" to username,
                                    "email" to email,
                                    "cpf" to cpf
                                )

                                firestore.collection("users").document(user!!.uid)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        showToast("Cadastro realizado com sucesso")
                                        val intent = Intent(this, SignupSuccessActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        showToast("Falha ao cadastrar usuário: ${e.message}")
                                    }
                            } else {
                                showToast("Falha ao cadastrar usuário: ${task.exception?.message}")
                            }
                        }
                } else {
                    showToast("Este CPF já está cadastrado")
                }
            }
            .addOnFailureListener { e ->
                showToast("Erro ao verificar CPF: ${e.message}")
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
