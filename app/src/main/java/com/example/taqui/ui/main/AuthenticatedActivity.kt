package com.example.taqui.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taqui.R
import com.example.taqui.ui.main.HomeFragment
import com.example.taqui.ui.main.FavoritesFragment
import com.example.taqui.ui.main.SettingsFragment
import com.example.taqui.ui.main.MessagesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AuthenticatedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticated)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_favorites -> {
                    loadFragment(FavoritesFragment())
                    true
                }
                R.id.action_eureka -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.action_central -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.action_messages -> {
                    loadFragment(MessagesFragment())
                    true
                }
                R.id.action_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}

