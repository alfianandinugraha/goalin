package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.goalin.service.AuthService
import com.example.goalin.service.UserService
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.ui.ButtonView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {
    private val scope = CoroutineScope(CoroutineName("ProfileScope") + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val emailTextView = findViewById<TextView>(R.id.email)
        val fullNameTextView = findViewById<TextView>(R.id.full_name)

        val logoutButton = findViewById<ButtonView>(R.id.logout_btn)
        val toEditProfileButton = findViewById<ButtonView>(R.id.to_edit_profile_btn)

        val loginActivity = Intent(baseContext, LoginActivity::class.java)
        val editProfileActivity = Intent(baseContext, EditProfileActivity::class.java)

        val authService = AuthService(this)
        val userService = UserService(this)

        logoutButton.setOnClickListener {
            startActivity(loginActivity)
            authService.logout()
        }

        toEditProfileButton.setOnClickListener {
            startActivity(editProfileActivity)
        }

        scope.launch {
            try {
                val response = userService.get()
                val user = response.payload
                withContext(Dispatchers.Main) {
                    fullNameTextView.text = user.fullName
                    emailTextView.text = user.email

                    editProfileActivity.putExtra("user", Gson().toJson(user))
                    toEditProfileButton.setOnClickListener {
                        startActivity(editProfileActivity)
                    }
                }
            } catch (err: ApiResponseException) {
                withContext(Dispatchers.Main) {
                    Toast
                        .makeText(this@ProfileActivity, err.message, Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        }
    }
}