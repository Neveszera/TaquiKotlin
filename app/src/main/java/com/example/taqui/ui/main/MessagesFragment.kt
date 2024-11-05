package com.example.taqui.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.taqui.R

class MessagesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        val btnBack = view.findViewById<TextView>(R.id.btn_back)

        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }
}
