package com.cmchat.util

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.cmchat.cmchat.R
import java.util.Calendar

class DatePickerFragment(private val listener: OnDateSetListener) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]
        return DatePickerDialog(
            requireActivity(), R.style.datepicker,
            listener, year, month, day
        )
    }

}