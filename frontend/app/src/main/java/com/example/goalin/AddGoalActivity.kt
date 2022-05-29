package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.goalin.model.ResponseStatus
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
    companion object {
        const val SUCCESS = 123
    }

    private val scope = CoroutineScope(CoroutineName("AddGoalScope") + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_goal)

        val categorySelectView = findViewById<SelectView<String>>(R.id.category_select)
        val nameTextView = findViewById<TextView>(R.id.name)
        val totalTextView = findViewById<TextView>(R.id.total)

        val addButton = findViewById<ButtonView>(R.id.add_btn)

        val categoryService = CategoryService(this)
        val goalService = ViewModelProvider(this).get(GoalService::class.java)

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

            lifecycleScope.launch {
                goalService.store(body)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            goalService.storeFlow.collect {
                when (it) {
                    is ResponseStatus.Loading -> {
                        addButton.isEnabled = false
                    }
                    is ResponseStatus.Success -> {
                        Toast
                            .makeText(this@AddGoalActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@AddGoalActivity, MainActivity::class.java)
                        setResult(SUCCESS, intent)
                        finish()
                    }
                    is ResponseStatus.Error -> {
                        addButton.isEnabled = true
                        Toast
                            .makeText(this@AddGoalActivity, it.message, Toast.LENGTH_SHORT)
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