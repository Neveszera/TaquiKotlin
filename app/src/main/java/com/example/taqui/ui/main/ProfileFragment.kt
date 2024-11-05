package com.example.taqui.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taqui.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var user: FirebaseUser
    private lateinit var btnSave: Button
    private lateinit var btnAvatar: ImageView
    private lateinit var usernameInput: TextView
    private lateinit var newPasswordInput: TextView
    private var selectedAvatarUri: Uri? = null

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedAvatarUri = it
                btnAvatar.setImageURI(selectedAvatarUri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        user = auth.currentUser!!

        btnSave = view.findViewById(R.id.btn_save)
        btnAvatar = view.findViewById(R.id.btn_avatar)
        usernameInput = view.findViewById(R.id.username_input)
        newPasswordInput = view.findViewById(R.id.new_password_input)

        loadUserInfo()

        btnAvatar.setOnClickListener {
            openImageChooser()
        }

        btnSave.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadUserInfo() {
        db.collection("users").document(user.uid).get().addOnSuccessListener { document ->
            if (document != null) {
                usernameInput.text = document.getString("username") ?: ""
            }
        }
    }

    private fun openImageChooser() {
        pickImageLauncher.launch("image/*")
    }

    private fun updateProfile() {
        val newUsername = usernameInput.text.toString()
        val newPassword = newPasswordInput.text.toString()

        db.collection("users").document(user.uid)
            .update("username", newUsername)
            .addOnSuccessListener {
                Toast.makeText(context, "Perfil atualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Falha ao atualizar o perfil", Toast.LENGTH_SHORT).show()
            }

        if (newPassword.isNotEmpty()) {
            user.updatePassword(newPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Senha atualizada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Falha ao atualizar a senha", Toast.LENGTH_SHORT).show()
                }
            }
        }

        selectedAvatarUri?.let { uri ->
            val avatarRef: StorageReference = storage.reference.child("avatars/${user.uid}")
            avatarRef.putFile(uri)
                .addOnSuccessListener {
                    Toast.makeText(context, "Avatar enviado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Falha ao enviar avatar", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
