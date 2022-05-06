package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goalin.service.AuthService
import com.example.goalin.views.ButtonView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val logoutButton = findViewById<ButtonView>(R.id.logout_btn)
        val toEditProfileButton = findViewById<ButtonView>(R.id.to_edit_profile_btn)

        val loginActivity = Intent(baseContext, LoginActivity::class.java)
        val editProfileActivity = Intent(baseContext, EditProfileActivity::class.java)

        val authService = AuthService(this)

        logoutButton.setOnClickListener {
            startActivity(loginActivity)
            authService.logout()
        }

        toEditProfileButton.setOnClickListener {
            startActivity(editProfileActivity)
        }
    }
}