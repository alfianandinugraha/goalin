package com.example.goalin.views

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import com.example.goalin.R

class EditText(context: Context, attrs: AttributeSet):
    AppCompatEditText(
        ContextThemeWrapper(context, R.style.style_edit_text),
        attrs,
        R.style.style_edit_text
    )