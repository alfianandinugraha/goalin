package com.example.goalin.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.goalin.R

class ProgressView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
    var text: String = ""
        set(value) {
            val textView = findViewById<TextView>(R.id.text)
            textView.text = value
            field = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_progress, this)

        val textAttr = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text") ?: "Memuat data"
        text = textAttr
    }
}