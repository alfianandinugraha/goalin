package com.example.goalin.ui

import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import com.airbnb.paris.extensions.style
import com.example.goalin.R
import java.util.Calendar

class DatePickerView(context: Context, attrs: AttributeSet):
    AppCompatEditText(
        ContextThemeWrapper(context, R.style.style_edit_text_theme),
        attrs
    ) {
    private val months = listOf(
        "Januari",
        "Februari",
        "Maret",
        "April",
        "Mei",
        "Juni",
        "Juli",
        "Agustus",
        "September",
        "November",
        "Desembar",
    )

    var calendar: Calendar? = null

    init {
        style(R.style.style_edit_text)

        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH)

        setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_baseline_calendar_today_black,
            0
        )

        if (calendar == null) {
            calendar = currentCalendar
            val dateText = "$currentDayOfMonth ${months[currentMonth]} $currentYear"
            setText(dateText)
        }

        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val dateText = "$dayOfMonth ${months[month]} $year"
                setText(dateText)

                val newCalendar = Calendar.getInstance()
                newCalendar.set(Calendar.YEAR, year)
                newCalendar.set(Calendar.MONTH, month)
                newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                calendar = newCalendar
            },
            currentYear, currentMonth, currentDayOfMonth
        )

        keyListener = null

        dialog.datePicker.minDate = currentCalendar.timeInMillis

        setOnClickListener {
            if (isFocused) {
                dialog.show()
            }
        }

        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) return@setOnFocusChangeListener
            dialog.show()
        }
    }
}