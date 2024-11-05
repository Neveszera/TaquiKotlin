package com.example.taqui.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.taqui.R
import com.example.taqui.ui.auth.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var switchNotifyPhone: Switch
    private lateinit var switchNoNotify1822: Switch
    private lateinit var switchRecommendations: Switch
    private lateinit var switchNotifyConnectedDevices: Switch
    private lateinit var switchHideThirdPartyNotifications: Switch
    private lateinit var switchKeepRecentSearch: Switch
    private lateinit var switchLoremIpsum: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Inicializar FirebaseAuth e SharedPreferences
        auth = FirebaseAuth.getInstance() // Corrigido aqui
        prefs = requireContext().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

        // Referências para os componentes
        val btnBack = view.findViewById<TextView>(R.id.btn_back)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnDeleteAccount = view.findViewById<TextView>(R.id.btn_delete_account)

        switchNotifyPhone = view.findViewById(R.id.switch_notify_phone)
        switchNoNotify1822 = view.findViewById(R.id.switch_no_notify_18_22)
        switchRecommendations = view.findViewById(R.id.switch_recommendations)
        switchNotifyConnectedDevices = view.findViewById(R.id.switch_notify_connected_devices)
        switchHideThirdPartyNotifications = view.findViewById(R.id.switch_hide_third_party_notifications)
        switchKeepRecentSearch = view.findViewById(R.id.switch_keep_recent_search)
        switchLoremIpsum = view.findViewById(R.id.switch_lorem_ipsum)

        // Carregar configurações salvas
        loadSettings()

        // Configurar o botão "Voltar"
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Configurar o botão "Salvar"
        btnSave.setOnClickListener {
            saveSettings()
        }

        // Configurar o botão "Deletar conta"
        btnDeleteAccount.setOnClickListener {
            confirmDeleteAccount()
        }

        return view
    }

    private fun loadSettings() {
        switchNotifyPhone.isChecked = prefs.getBoolean("notifyPhone", false)
        switchNoNotify1822.isChecked = prefs.getBoolean("noNotify1822", false)
        switchRecommendations.isChecked = prefs.getBoolean("recommendations", false)
        switchNotifyConnectedDevices.isChecked = prefs.getBoolean("notifyConnectedDevices", false)
        switchHideThirdPartyNotifications.isChecked = prefs.getBoolean("hideThirdPartyNotifications", false)
        switchKeepRecentSearch.isChecked = prefs.getBoolean("keepRecentSearch", false)
        switchLoremIpsum.isChecked = prefs.getBoolean("loremIpsum", false)
    }

    private fun saveSettings() {
        with(prefs.edit()) {
            putBoolean("notifyPhone", switchNotifyPhone.isChecked)
            putBoolean("noNotify1822", switchNoNotify1822.isChecked)
            putBoolean("recommendations", switchRecommendations.isChecked)
            putBoolean("notifyConnectedDevices", switchNotifyConnectedDevices.isChecked)
            putBoolean("hideThirdPartyNotifications", switchHideThirdPartyNotifications.isChecked)
            putBoolean("keepRecentSearch", switchKeepRecentSearch.isChecked)
            putBoolean("loremIpsum", switchLoremIpsum.isChecked)
            apply()
        }
    }

    private fun confirmDeleteAccount() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar exclusão")
            .setMessage("Tem certeza de que deseja deletar sua conta?")
            .setPositiveButton("Sim") { _, _ -> deleteAccount() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser

        if (user != null) {
            // Primeiro, excluir os dados do Firestore
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid) // Substitua "users" pelo nome correto da sua coleção

            userDocRef.delete().addOnCompleteListener { deleteTask ->
                if (deleteTask.isSuccessful) {
                    // Dados excluídos com sucesso, agora excluir a autenticação do usuário
                    user.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Desconectar e redirecionar para a tela de login
                            auth.signOut()
                            redirectToLogin() // Chama o método para redirecionar
                        } else {
                            showSnackbar("Erro ao deletar a conta. Tente novamente.")
                        }
                    }
                } else {
                    showSnackbar("Erro ao excluir dados do Firestore. Tente novamente.")
                }
            }
        } else {
            showSnackbar("Usuário não autenticado.")
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}