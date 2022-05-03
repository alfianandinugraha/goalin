package com.example.goalin.views

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.goalin.GoalActivity
import com.example.goalin.R

class GoalItemView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.view_goal_item, this)

        val goalActivity = Intent(context, GoalActivity::class.java)

        setOnClickListener {
            context.startActivity(goalActivity)
        }
    }
}