package com.example.goalin.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.goalin.R

class BackView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.view_back,this)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BackView)
        val backActivity = typedArray.getString(R.styleable.BackView_backTo)

        val backButton = findViewById<LinearLayout>(R.id.back_btn)
        backButton.setOnClickListener {
            val activity: Activity = context as Activity
            if (backActivity == null) {
                activity.finish()
                return@setOnClickListener
            }

            val className = Class.forName("com.example.goalin$backActivity")
            val backIntent = Intent(context, className)
            activity.startActivity(backIntent)
        }
    }
}