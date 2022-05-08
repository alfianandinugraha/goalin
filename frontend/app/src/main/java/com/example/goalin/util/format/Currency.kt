package com.example.goalin.util.format

import java.text.NumberFormat
import java.util.Locale

class Currency {
    companion object {
        fun format(amount: Float): String {
            return NumberFormat.getIntegerInstance(Locale.GERMAN).format(amount)
        }

        fun rupiah(amount: Float): String {
            return "Rp${format(amount)}"
        }
    }
}