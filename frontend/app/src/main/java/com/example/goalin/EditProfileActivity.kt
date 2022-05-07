package com.example.goalin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goalin.model.User
import com.example.goalin.views.EditTextView
import com.google.gson.Gson

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val userJSON = intent.getStringExtra("user")
        val user = Gson().fromJson(userJSON, User::class.java)

        val fullNameEditText = findViewById<EditTextView>(R.id.full_name_edit_text)
        val emailEditText = findViewById<EditTextView>(R.id.email_edit_text)

        if (userJSON != null) {
            fullNameEditText.setText(user.fullName)
            emailEditText.setText(user.email)
        }
    }
}