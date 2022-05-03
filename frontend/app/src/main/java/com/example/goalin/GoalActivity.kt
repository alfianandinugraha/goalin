package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goalin.views.ButtonView

class GoalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val editButton = findViewById<ButtonView>(R.id.edit_btn)
        val editGoalActivity = Intent(this, EditGoalActivity::class.java)

        editButton.setOnClickListener {
            startActivity(editGoalActivity)
        }
    }
}