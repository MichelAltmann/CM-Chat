package com.cmchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        forgotPasswordClick()

        onLoginClick()

    }

    private fun onLoginClick() {
        val userInput = binding.usernameInput
        val passInput = binding.passwordInput

        binding.loginBtn.setOnClickListener {
            if (userInput.text.isNullOrEmpty()){
                userInput.error = "Invalid Username"
                userInput.requestFocus()
                return@setOnClickListener
            }
            if (passInput.text.isNullOrEmpty()){
                passInput.error = "Invalid Password"
                passInput.requestFocus()
                return@setOnClickListener
            }

            val username = userInput.text.toString()
            val password = passInput.text.toString()

            if (username.equals("Mijas") && password.equals("Bolas")){
                val intent = Intent(this, ChatActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                userInput.error = "Invalid Username or Password"
                userInput.requestFocus()
            }
        }
    }

    private fun forgotPasswordClick() {
        binding.forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "I don't care 😂", Toast.LENGTH_SHORT).show()
        }
    }
}