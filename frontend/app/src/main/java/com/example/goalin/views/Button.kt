package com.example.goalin.views

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.example.goalin.R

class Button(context: Context, attrs: AttributeSet): CompoundButton(context, attrs) {
    init {
        textAlignment = TEXT_ALIGNMENT_CENTER
        setTextAppearance(R.style.button_text)

        background = ContextCompat.getDrawable(context, R.drawable.button_state)

        setOnClickListener {
            isPressed = true
        }
    }
}