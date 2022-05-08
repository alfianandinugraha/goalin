package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.goalin.service.AuthService
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.EditTextView
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    val scope = CoroutineScope(CoroutineName("RegisterScope") + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val fullNameEditText = findViewById<EditTextView>(R.id.full_name)
        val emailEditText = findViewById<EditTextView>(R.id.email)
        val passwordEditText = findViewById<EditTextView>(R.id.password)
        val registerButton = findViewById<ButtonView>(R.id.register_btn)

        val loginTextView = findViewById<TextView>(R.id.login_text)
        val loginActivity = Intent(this, LoginActivity::class.java)

        val mainActivity = Intent(this, MainActivity::class.java)

        loginTextView.setOnClickListener {
            startActivity(loginActivity)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

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

            val authServices = AuthService(this)
            registerButton.isEnabled = false

            scope.launch {
                try {
                    authServices.register(fullName, email, password)
                    startActivity(mainActivity)
                } catch (err: ApiResponseException) {
                    withContext(Dispatchers.Main) {
                        registerButton.isEnabled = true
                        Toast.makeText(
                            this@RegisterActivity,
                            err.response().message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}