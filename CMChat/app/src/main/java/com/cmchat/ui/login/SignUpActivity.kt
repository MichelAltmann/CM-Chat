package com.cmchat.ui.login

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.DatePicker
import androidx.core.widget.doOnTextChanged
import com.cmchat.DatePickerFragment
import com.cmchat.cmchat.databinding.ActivitySignUpBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale


class SignUpActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var _binding : ActivitySignUpBinding? = null
    private val binding get() = _binding!!
    private var calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onSignUpClick()

        onCancelClick()

        calendarFabClick()

        onBirthdateText()
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
        val format = "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat(format, Locale.CANADA)
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



        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        calendar = GregorianCalendar(year,month,day)
        updateBirthdate()
    }


}