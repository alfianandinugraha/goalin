package com.example.goalin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.goalin.service.CategoryService
import com.example.goalin.service.GoalService
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.SelectView
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddGoalActivity : AppCompatActivity() {
    private val scope = CoroutineScope(CoroutineName("AddGoalScope") + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_goal)

        val categorySelectView = findViewById<SelectView<String>>(R.id.category_select)
        val nameTextView = findViewById<TextView>(R.id.name)
        val totalTextView = findViewById<TextView>(R.id.total)

        val addButton = findViewById<ButtonView>(R.id.add_btn)

        val categoryService = CategoryService(this)
        val goalService = GoalService(this)

        scope.launch {
            try {
                val categories = categoryService.getAll()
                withContext(Dispatchers.Main) {
                    categorySelectView.options = categories.payload.map {
                        SelectView.Option(it.name, it.id)
                    }
                }
            } catch (err: ApiResponseException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddGoalActivity,
                        err.message ?: "Gagal memuat list kategori",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

        addButton.setOnClickListener {
            val name = nameTextView.text.toString()
            val total = totalTextView.text.toString().toFloatOrNull()
            val categoryId = categorySelectView.selectedOption?.value

            if (name.isEmpty() || total == null || categoryId == null) {
                Toast
                    .makeText(this, "Harap isi semua bidang diatas", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val body = GoalService.CreateGoalBodyRequest(
                name = name,
                categoryId = categoryId,
                total = total
            )

            scope.launch {
                try {
                    goalService.store(body)
                    withContext(Dispatchers.Main) {
                        Toast
                            .makeText(this@AddGoalActivity, "Berhasil menambahkan goal!", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                } catch (err: ApiResponseException) {
                    withContext(Dispatchers.Main) {
                        Toast
                            .makeText(this@AddGoalActivity, err.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}