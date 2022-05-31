package com.example.goalin.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.goalin.R

class EmptyListView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
    var text: String = ""
        set(value) {
            val textView = findViewById<TextView>(R.id.text)
            textView.text = value
            field = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_empty_list, this)

        val textAttr = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text") ?: "Data tidak ditemukan"
        text = textAttr
    }
}