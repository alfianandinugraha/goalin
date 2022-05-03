package com.example.goalin.views

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import com.airbnb.paris.extensions.style
import com.example.goalin.R

class EditTextView(context: Context, attrs: AttributeSet):
    AppCompatEditText(
        ContextThemeWrapper(context, R.style.style_edit_text_theme),
        attrs
    ) {
        init {
            style(R.style.style_edit_text)
        }
    }