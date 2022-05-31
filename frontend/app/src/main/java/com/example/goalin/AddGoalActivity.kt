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
import com.example.goalin.service.CategoryService
import com.example.goalin.service.GoalService
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.FloatingProgressView
import com.example.goalin.ui.SelectView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddGoalActivity : AppCompatActivity() {
    companion object {
        const val SUCCESS = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_goal)

        val categorySelectView = findViewById<SelectView<String>>(R.id.category_select)
        val nameTextView = findViewById<TextView>(R.id.name)
        val totalTextView = findViewById<TextView>(R.id.total)

        val floatingProgress = findViewById<FloatingProgressView>(R.id.floating_progress)
        val addButton = findViewById<ButtonView>(R.id.add_btn)

        val categoryService = ViewModelProvider(this).get(CategoryService::class.java)
        val goalService = ViewModelProvider(this).get(GoalService::class.java)

        lifecycleScope.launch {
            categoryService.getAll()
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
                        floatingProgress.visibility = View.VISIBLE
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
                        floatingProgress.visibility = View.GONE
                        Toast
                            .makeText(this@AddGoalActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            categoryService.getAllFlow.collect {
                when (it) {
                    is ResponseStatus.Loading -> {
                        addButton.isEnabled = false
                    }
                    is ResponseStatus.Success -> {
                        addButton.isEnabled = true
                        categorySelectView.options = it.payload.map { category ->
                            SelectView.Option(category.name, category.id)
                        }
                    }
                    is ResponseStatus.Error -> {
                        Toast
                            .makeText(this@AddGoalActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
        }
    }
}