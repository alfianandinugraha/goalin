package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.goalin.model.ResponseStatus
import com.example.goalin.service.AuthService
import com.example.goalin.service.UserService
import com.example.goalin.ui.ButtonView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val emailTextView = findViewById<TextView>(R.id.email)
        val fullNameTextView = findViewById<TextView>(R.id.full_name)

        val logoutButton = findViewById<ButtonView>(R.id.logout_btn)
        val toEditProfileButton = findViewById<ButtonView>(R.id.to_edit_profile_btn)

        val loginActivity = Intent(baseContext, LoginActivity::class.java)
        val editProfileActivity = Intent(baseContext, EditProfileActivity::class.java)

        val authService = AuthService(application)
        val userService = ViewModelProvider(this).get(UserService::class.java)

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                EditProfileActivity.SUCCESS -> {
                    lifecycleScope.launch {
                        userService.getDetail()
                    }
                }
            }
        }

        logoutButton.setOnClickListener {
            startActivity(loginActivity)
            authService.logout()
        }

        toEditProfileButton.setOnClickListener {
            startForResult.launch(editProfileActivity)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            userService.getDetail()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            userService.getDetailFlow.collect {
                when(it) {
                    is ResponseStatus.Loading -> {}
                    is ResponseStatus.Success -> {
                        fullNameTextView.text = it.payload.fullName
                        emailTextView.text = it.payload.email

                        editProfileActivity.putExtra("user", Gson().toJson(it.payload))
                        toEditProfileButton.setOnClickListener {
                            startForResult.launch(editProfileActivity)
                        }
                    }
                    is ResponseStatus.Error -> {
                        Toast
                            .makeText(this@ProfileActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
        }
    }
}