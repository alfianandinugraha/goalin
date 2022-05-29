package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalin.model.ResponseStatus
import com.example.goalin.service.GoalService
import com.example.goalin.ui.GoalsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var startActivityForResult:
            ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toAddGoalButton = findViewById<FloatingActionButton>(R.id.to_add_goal_btn)
        val toProfileButton = findViewById<LinearLayout>(R.id.to_profile_btn)
        val goalsRecyclerView = findViewById<RecyclerView>(R.id.goals)

        toAddGoalButton.setOnClickListener {
            startActivity(
                Intent(this, AddGoalActivity::class.java)
            )
        }

        toProfileButton.setOnClickListener {
            startActivity(
                Intent(this, ProfileActivity::class.java)
            )
        }

        val goalService = ViewModelProvider(this).get(GoalService::class.java)
        val goalsAdapter = GoalsAdapter(this)

        startActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                GoalActivity.CHANGED, GoalActivity.DELETED, AddGoalActivity.SUCCESS -> {
                    lifecycleScope.launch {
                        goalService.getAll()
                    }
                }
            }
        }

        lifecycleScope.launch {
            goalService.getAll()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            goalService.goalsFlow.collect {
                when (it) {
                    is ResponseStatus.Loading -> {}
                    is ResponseStatus.Success -> {
                        goalsRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                        goalsAdapter.goals = it.payload.toMutableList()
                        goalsRecyclerView.adapter = goalsAdapter
                    }
                    is ResponseStatus.Error -> {
                        Toast
                            .makeText(this@MainActivity, "Gagal mendapatkan list goal", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun startActivity(intent: Intent?) {
        startActivityForResult.launch(intent)
    }
}