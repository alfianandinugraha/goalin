package com.example.goalin.views

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import androidx.appcompat.view.ContextThemeWrapper
import com.example.goalin.R

class Button(context: Context, attrs: AttributeSet): CompoundButton(ContextThemeWrapper(context, R.style.style_button), attrs, R.style.style_button) {
    init {
        setOnClickListener {
            isPressed = true
        }
    }
}