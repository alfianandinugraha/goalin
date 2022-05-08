package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.service.AuthService
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.EditTextView
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private val scope = CoroutineScope(CoroutineName("LoginScope") + Dispatchers.IO)

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

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua bidang diatas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val authServices = AuthService(this)
            loginButton.isEnabled = false

            scope.launch {
                try {
                    authServices.login(email, password)
                    startActivity(mainActivity)
                } catch (err: ApiResponseException) {
                    withContext(Dispatchers.Main) {
                        loginButton.isEnabled = true
                        Toast.makeText(
                            this@LoginActivity,
                            err.response().message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBackPressed() {}
}