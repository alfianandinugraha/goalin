package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toAddGoalButton = findViewById<FloatingActionButton>(R.id.to_add_goal_btn)
        val toProfileButton = findViewById<LinearLayout>(R.id.to_profile_btn)

        val addGoalActivity = Intent(this, AddGoalActivity::class.java)
        val profileActivity = Intent(this, ProfileActivity::class.java)

        toAddGoalButton.setOnClickListener {
            startActivity(addGoalActivity)
        }

        toProfileButton.setOnClickListener {
            startActivity(profileActivity)
        }
    }
}