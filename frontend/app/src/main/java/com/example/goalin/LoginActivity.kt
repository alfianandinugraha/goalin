package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.goalin.model.ResponseStatus
import com.example.goalin.service.AuthService
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.EditTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditTextView>(R.id.email)
        val registerTextView = findViewById<TextView>(R.id.register_text)
        val passwordEditText = findViewById<EditTextView>(R.id.password)
        val loginButton = findViewById<ButtonView>(R.id.login_btn)

        val mainActivity = Intent(this, MainActivity::class.java)
        val registerActivity = Intent(this, RegisterActivity::class.java)

        registerTextView.setOnClickListener {
            startActivity(registerActivity)
        }

        val authServices = ViewModelProvider(this).get(AuthService::class.java)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast
                    .makeText(this, "Harap isi semua bidang diatas", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                authServices.login(email, password)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            authServices.loginFlow.collect {
                when(it) {
                    is ResponseStatus.Loading -> {
                        loginButton.isEnabled = false
                    }
                    is ResponseStatus.Success -> {
                        startActivity(mainActivity)
                        finish()
                    }
                    is ResponseStatus.Error -> {
                        Toast
                            .makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                        loginButton.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onBackPressed() {}
}