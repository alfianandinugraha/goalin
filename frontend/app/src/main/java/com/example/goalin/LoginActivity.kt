package com.example.goalin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.goalin.views.ButtonView
import com.example.goalin.views.EditTextView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditTextView>(R.id.email)
        val passwordEditText = findViewById<EditTextView>(R.id.password)
        val loginButton = findViewById<ButtonView>(R.id.login_btn)

        loginButton.setOnClickListener {
            Log.d("Event", "Click login button...")
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            Log.d("Input", "email: $email, password: $password")
        }
    }
}