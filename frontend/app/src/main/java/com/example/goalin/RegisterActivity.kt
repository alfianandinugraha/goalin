package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.goalin.model.ResponseStatus
import com.example.goalin.service.AuthService
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.EditTextView
import com.example.goalin.ui.FloatingProgressView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val fullNameEditText = findViewById<EditTextView>(R.id.full_name)
        val emailEditText = findViewById<EditTextView>(R.id.email)
        val passwordEditText = findViewById<EditTextView>(R.id.password)
        val registerButton = findViewById<ButtonView>(R.id.register_btn)
        val floatingProgress = findViewById<FloatingProgressView>(R.id.floating_progress)

        val loginTextView = findViewById<TextView>(R.id.login_text)
        val loginActivity = Intent(this, LoginActivity::class.java)

        val mainActivity = Intent(this, MainActivity::class.java)

        loginTextView.setOnClickListener {
            startActivity(loginActivity)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val authService = ViewModelProvider(this).get(AuthService::class.java)

        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val isInvalid = fullName.isEmpty() || email.isEmpty() || password.isEmpty()

            if (isInvalid) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Harap isi semua bidang diatas",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                authService.register(fullName, email, password)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            authService.registerFlow.collect {
                when (it) {
                    is ResponseStatus.Loading -> {
                        floatingProgress.visibility = View.VISIBLE
                    }
                    is ResponseStatus.Success -> {
                        startActivity(mainActivity)
                        finish()
                    }
                    is ResponseStatus.Error -> {
                        floatingProgress.visibility = View.GONE
                        Toast.makeText(
                            this@RegisterActivity,
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}