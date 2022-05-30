package com.example.goalin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.goalin.model.Goal
import com.example.goalin.model.ResponseStatus
import com.example.goalin.service.CategoryService
import com.example.goalin.service.GoalService
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.SelectView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditGoalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_goal)

        val goalJSON = intent.getStringExtra("goal")

        val goal = Gson().fromJson(goalJSON, Goal::class.java)

        val nameTextView = findViewById<TextView>(R.id.name)
        val totalTextView = findViewById<TextView>(R.id.total)
        val saveButton = findViewById<ButtonView>(R.id.save_btn)
        val categorySelectView = findViewById<SelectView<String>>(R.id.category_select)

        val goalService = ViewModelProvider(this).get(GoalService::class.java)
        val categoryService = ViewModelProvider(this).get(CategoryService::class.java)

        nameTextView.text = goal.name
        totalTextView.text = goal.total.toString()

        saveButton.setOnClickListener {
            finish()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            categoryService.getAll()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            categoryService.getAllFlow.collect {
                when (it) {
                    is ResponseStatus.Loading -> {
                        saveButton.isEnabled = false
                    }
                    is ResponseStatus.Success -> {
                        saveButton.isEnabled = true
                        categorySelectView.selectedOption = SelectView.Option(goal.category.name, goal.category.id)
                        categorySelectView.options = it.payload.map { category ->
                            SelectView.Option(category.name, category.id)
                        }
                    }
                    is ResponseStatus.Error -> {
                        Toast
                            .makeText(this@EditGoalActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
        }
    }
}