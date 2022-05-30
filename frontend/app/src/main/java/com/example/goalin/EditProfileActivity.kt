package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.goalin.model.ResponseStatus
import com.example.goalin.model.User
import com.example.goalin.service.UserService
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.EditTextView
import com.google.gson.Gson
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {
    companion object {
        const val SUCCESS: Int = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val userJSON = intent.getStringExtra("user")
        val user = Gson().fromJson(userJSON, User::class.java)

        val fullNameEditText = findViewById<EditTextView>(R.id.full_name_edit_text)
        val emailEditText = findViewById<EditTextView>(R.id.email_edit_text)
        val saveBtn = findViewById<ButtonView>(R.id.save_btn)

        val userService = ViewModelProvider(this).get(UserService::class.java)

        if (userJSON != null) {
            fullNameEditText.setText(user.fullName)
            emailEditText.setText(user.email)
        }

        saveBtn.setOnClickListener {
            val email = emailEditText.text.toString()
            val fullName = fullNameEditText.text.toString()

            if (email.isEmpty() || fullName.isEmpty()) {
                Toast
                    .makeText(this, "Harap isi semua bidang diatas", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                userService.update(fullName, email)
            }
        }

        lifecycleScope.launch {
            userService.updateFlow.collect {
                when(it) {
                    is ResponseStatus.Loading -> {
                        saveBtn.isEnabled = false
                    }
                    is ResponseStatus.Success -> {
                        Toast
                            .makeText(this@EditProfileActivity, "Berhasil memperbarui profil", Toast.LENGTH_SHORT)
                            .show()
                        setResult(SUCCESS, intent)
                        finish()
                    }
                    is ResponseStatus.Error -> {
                        Toast
                            .makeText(this@EditProfileActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                        saveBtn.isEnabled = true
                    }
                }
            }
        }
    }
}