package com.example.goalin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.goalin.service.CategoryService
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.views.ButtonView
import com.example.goalin.views.SelectView
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

        val addButton = findViewById<ButtonView>(R.id.add_btn)

        addButton.setOnClickListener {
            Log.d("Option", categorySelectView.selectedOption?.value.toString())
        }

        val categoryService = CategoryService(this)

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
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}