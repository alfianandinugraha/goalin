package com.example.goalin.ui

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.goalin.R

class BackView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var view: LinearLayout;
    private val baseContext = context;

    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.view_back,this)

        view = findViewById(R.id.back_btn)

        setOnClickListener {
            back()
        }
    }

    fun back() {
        val activity: Activity = baseContext as Activity
        activity.finish()
    }

    fun setOnClickListener(listener: (view: View) -> Unit) {
        view.setOnClickListener {
            listener(this)
        }
    }
}