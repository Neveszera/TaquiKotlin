package com.example.taqui.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.taqui.R
import com.example.taqui.ui.main.AuthenticatedActivity
import com.example.taqui.ui.main.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HeaderFragment : Fragment() {

    private lateinit var greetingTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_header, container, false)

        greetingTextView = view.findViewById(R.id.greeting_text)
        usernameTextView = view.findViewById(R.id.username_text)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupGreetingAndUsername()

        usernameTextView.setOnClickListener {
            loadFragment(ProfileFragment())
        }

        val avatarImageView = view.findViewById<ImageView>(R.id.avatar_image)
        avatarImageView.setOnClickListener {
            loadFragment(ProfileFragment())
        }

        return view
    }

    private fun setupGreetingAndUsername() {
        val currentHour = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
        val greeting = when {
            currentHour in 5..11 -> "bom dia,"
            currentHour in 12..17 -> "boa tarde,"
            else -> "boa noite,"
        }
        greetingTextView.text = greeting

        val currentUser = auth.currentUser
        currentUser?.let {
            firestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    val username = document.getString("username")
                    usernameTextView.text = username ?: "Usuário"
                }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        (activity as? AuthenticatedActivity)?.loadFragment(fragment)
    }
}