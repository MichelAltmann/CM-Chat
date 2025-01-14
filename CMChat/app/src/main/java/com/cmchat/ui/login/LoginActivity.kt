package com.cmchat.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cmchat.cmchat.databinding.ActivityLoginBinding
import com.cmchat.model.LoginRequest
import com.cmchat.ui.login.signup.SignUpActivity
import com.cmchat.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigInteger
import java.security.MessageDigest


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

        onRegisterClick()

        observeUser()

    }

    private fun onRegisterClick() {
        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun passwordHasher(passwordEt: EditText): String {
        var crudePassword = passwordEt.text.toString()
        val md = MessageDigest.getInstance("SHA-256")
        val bigPassword = BigInteger(1, md.digest(crudePassword.toByteArray()))
        val password = bigPassword.toString()
        crudePassword = ""
        Log.i(TAG, "passwordHasher: " + password)
        return password
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
            Log.i(TAG, "observeUser: " + it.message)
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
            val password = passwordHasher(passInput)


            viewModel.authenticateUser(LoginRequest(username, password))
        }
    }

    private fun forgotPasswordClick() {
        binding.forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "I don't care 😂", Toast.LENGTH_SHORT).show()
        }
    }

}
