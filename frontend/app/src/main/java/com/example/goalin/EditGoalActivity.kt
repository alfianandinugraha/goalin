package com.example.goalin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goalin.views.ButtonView

class EditGoalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_goal)

        val saveButton = findViewById<ButtonView>(R.id.save_btn)

        saveButton.setOnClickListener {
            finish()
        }
    }
}