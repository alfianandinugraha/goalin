package com.example.goalin.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.goalin.R

class FloatingProgressView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_floating_progress, this)
    }
}