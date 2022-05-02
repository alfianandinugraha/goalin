package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goalin.views.Button

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val logoutButton = findViewById<Button>(R.id.logout_btn)
        val toEditProfileButton = findViewById<Button>(R.id.to_edit_profile_btn)

        val loginActivity = Intent(baseContext, LoginActivity::class.java)
        val editProfileActivity = Intent(baseContext, EditProfileActivity::class.java)

        logoutButton.setOnClickListener {
            startActivity(loginActivity)
        }

        toEditProfileButton.setOnClickListener {
            startActivity(editProfileActivity)
        }
    }
}