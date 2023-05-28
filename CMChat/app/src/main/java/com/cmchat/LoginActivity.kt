package com.cmchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.ActivityLoginBinding

private var _binding : ActivityLoginBinding? = null
private val binding get() = _binding!!

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}