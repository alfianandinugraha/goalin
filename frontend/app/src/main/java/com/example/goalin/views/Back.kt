package com.example.goalin.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.goalin.R

class Back(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.view_back,this)

        setOnClickListener {
            Log.d("Event", "Back...")
            val activity: Activity = context as Activity
            activity.finish()
        }
    }
}