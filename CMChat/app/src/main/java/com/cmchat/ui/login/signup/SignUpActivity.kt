package com.cmchat.ui.login.signup

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import com.cmchat.DatePickerFragment
import com.cmchat.cmchat.databinding.ActivitySignUpBinding
import com.cmchat.model.User
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale


class SignUpActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var _binding : ActivitySignUpBinding? = null
    private val binding get() = _binding!!
    private var calendar = Calendar.getInstance()

    private val format = "dd/MM/yyyy"
    private val dateFormat = SimpleDateFormat(format, Locale.getDefault())

    private val viewModel by viewModel<SignupViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onSignUpClick()

        onCancelClick()

        calendarFabClick()

        onBirthdateText()

        signUpObserver()
    }

    private fun signUpObserver() {
        viewModel.message.observe(this) {
            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            finish()
        }
        viewModel.error.observe(this){
            when (it.message){
                "Email already in use" -> {
                    binding.signUpEmailInput.error = "Email already in use"
                    binding.signUpEmailInput.requestFocus()
                }
                "Username already in use" -> {
                    binding.signUpUsernameInput.error = "Username already in use"
                    binding.signUpUsernameInput.requestFocus()
                }
            }
        }
    }

    private fun onBirthdateText() {
        val birthdateEt = binding.signUpBirthdateInput
        birthdateEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                var text = s.toString()
                if (text.isNotEmpty() && text.last() != '/') {
                    val formattedText = StringBuilder(text)
                    when (text.length) {
                        3, 6 -> {
                            formattedText.insert(text.length - 1, "/")
                            birthdateEt.setText(formattedText.toString())
                            birthdateEt.setSelection(formattedText.length)
                        }
                    }
                }
            }

        })
    }

    private fun calendarFabClick() {
        binding.signUpBirthdateCalendarFab.setOnClickListener {
            val datePicker = DatePickerFragment()
            datePicker.show(supportFragmentManager, "Date dialog")
        }
    }

    private fun updateBirthdate() {
        binding.signUpBirthdateInput.setText(dateFormat.format(calendar.time))
    }

    private fun onCancelClick() {
        binding.signUpBtnCancel.setOnClickListener {
            finish()
        }
    }


    fun onSignUpClick() {
        val usernameEt = binding.signUpUsernameInput
        val emailEt = binding.signUpEmailInput
        val birthdateEt = binding.signUpBirthdateInput
        val passwordEt = binding.signUpPasswordInput
        val repeatPasswordEt = binding.signUpRepeatPasswordInput
        binding.signUpBtnSignup.setOnClickListener {
            val error = "Insert a valid value."
            if (usernameEt.text.isNullOrEmpty()){
                usernameEt.error = error
                usernameEt.requestFocus()
                return@setOnClickListener
            }

            if (emailEt.text.isNullOrEmpty()){
                emailEt.error = error
                emailEt.requestFocus()
                return@setOnClickListener
            }

            if (birthdateEt.text.isNullOrEmpty()){
                birthdateEt.error = error
                birthdateEt.requestFocus()
                return@setOnClickListener
            }

            if (passwordEt.text.isNullOrEmpty()){
                passwordEt.error = error
                passwordEt.requestFocus()
                return@setOnClickListener
            }

            if (repeatPasswordEt.text.isNullOrEmpty()){
                repeatPasswordEt.error = error
                repeatPasswordEt.requestFocus()
                return@setOnClickListener
            }

            if (!passwordEt.text.toString().equals(repeatPasswordEt.text.toString())){
                passwordEt.error = "Passwords doesn't match."
                passwordEt.requestFocus()
                return@setOnClickListener
            }

            val username = usernameEt.text.toString()
            val email = emailEt.text.toString()
            val date = dateFormat.parse(birthdateEt.text.toString())

            val currentDay = Calendar.getInstance()
            val createDate = Date(currentDay.timeInMillis)
            // Hashing the password
            val crudePassword = passwordEt.text.toString()
            val md = MessageDigest.getInstance("SHA-256")
            val bigPassword = BigInteger(1, md.digest(crudePassword.toByteArray()))
            val password = bigPassword.toString()


            val user = User(0, email, username,password, date, null, null, null, createDate, 0, 0)

            Log.i(TAG, "onSignUpClick: " + user.birthday.toString())
            viewModel.signup(user)
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        calendar = GregorianCalendar(year,month,day)
        updateBirthdate()
    }


}