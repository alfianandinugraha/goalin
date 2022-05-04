package com.example.goalin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goalin.views.ButtonView

class AddTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val saveButton = findViewById<ButtonView>(R.id.save_btn)

        saveButton.setOnClickListener {
            finish()
        }
    }
}