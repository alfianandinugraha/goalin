package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalin.model.Goal
import com.example.goalin.service.GoalService
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.ui.GoalsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var startActivityForResult:
            ActivityResultLauncher<Intent>

    private val scope = CoroutineScope(CoroutineName("MainScope") + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toAddGoalButton = findViewById<FloatingActionButton>(R.id.to_add_goal_btn)
        val toProfileButton = findViewById<LinearLayout>(R.id.to_profile_btn)
        val goalsRecyclerView = findViewById<RecyclerView>(R.id.goals)

        val addGoalActivity = Intent(this, AddGoalActivity::class.java)
        val profileActivity = Intent(this, ProfileActivity::class.java)

        toAddGoalButton.setOnClickListener {
            startActivity(addGoalActivity)
        }

        toProfileButton.setOnClickListener {
            startActivity(profileActivity)
        }

        val goalService = GoalService(this)

        startActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                GoalActivity.CHANGED -> {
                    val value = it.data?.getStringExtra("goal")
                    val goal = Gson().fromJson(value, Goal::class.java)
                    Log.d("Event", value.toString())
                }
            }
        }

        scope.launch {
            try {
                val response = goalService.getAll()
                val goals = response.payload
                withContext(Dispatchers.Main) {
                    goalsRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    goalsRecyclerView.adapter = GoalsAdapter(this@MainActivity, goals)
                }
            } catch (err: ApiResponseException) {

            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun startActivity(intent: Intent?) {
        startActivityForResult.launch(intent)
    }
}