package com.cmchat.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cmchat.cmchat.databinding.ActivityLoginBinding
import com.cmchat.model.LoginRequest
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        forgotPasswordClick()

        onLoginClick()
        observeUser()

    }

    private fun observeUser() {
        viewModel.user.observe(this) {
            it?.let {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        viewModel.error.observe(this) {
            binding.usernameInput.error = "Invalid Username or Password"
            binding.usernameInput.requestFocus()
        }
    }


    private fun onLoginClick() {
        val userInput = binding.usernameInput
        val passInput = binding.passwordInput


        binding.loginBtn.setOnClickListener {

            if (userInput.text.isNullOrEmpty()) {
                userInput.error = "Invalid Username"
                userInput.requestFocus()
                return@setOnClickListener
            }
            if (passInput.text.isNullOrEmpty()) {
                passInput.error = "Invalid Password"
                passInput.requestFocus()
                return@setOnClickListener
            }

            val username = userInput.text.toString()
            val password = passInput.text.toString()

            viewModel.authenticateUser(LoginRequest(username, password))
        }
    }

    private fun forgotPasswordClick() {
        binding.forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "I don't care 😂", Toast.LENGTH_SHORT).show()
        }
    }

}
