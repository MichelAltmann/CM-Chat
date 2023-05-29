package com.cmchat.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {

    private var _binding : ActivitySignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }




}