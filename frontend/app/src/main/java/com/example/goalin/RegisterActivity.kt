package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val loginTextView = findViewById<TextView>(R.id.login_text)
        val loginActivity = Intent(this, LoginActivity::class.java)

        loginTextView.setOnClickListener {
            startActivity(loginActivity)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}