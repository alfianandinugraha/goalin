package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.goalin.model.Goal
import com.example.goalin.util.format.Currency
import com.example.goalin.ui.ButtonView
import com.google.gson.Gson
import kotlin.math.abs

class GoalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val goalJSON = intent.getStringExtra("goal")

        val goal = Gson().fromJson(goalJSON, Goal::class.java)

        val amountText = Currency.rupiah(goal.amount)
        val totalText = Currency.rupiah(goal.total)
        val minusText = "Tersisa ${Currency.rupiah(abs(goal.amount - goal.total))} lagi"
        val notesText = goal.notes

        val nameTextView = findViewById<TextView>(R.id.name)
        val amountTextView = findViewById<TextView>(R.id.amount)
        val totalTextView = findViewById<TextView>(R.id.total)
        val minusTextView = findViewById<TextView>(R.id.minus)
        val notesTextView = findViewById<TextView>(R.id.notes)

        val rangeThumbView = findViewById<View>(R.id.range_thumb)

        val editButton = findViewById<ButtonView>(R.id.edit_btn)
        val addTransactionButton = findViewById<Button>(R.id.add_transaction)
        val editGoalActivity = Intent(this, EditGoalActivity::class.java)
        val addTransactionActivity = Intent(this, AddTransactionActivity::class.java)

        editButton.setOnClickListener {
            startActivity(editGoalActivity)
        }

        addTransactionButton.setOnClickListener {
            addTransactionActivity.putExtra("goal", goalJSON)
            startActivity(addTransactionActivity)
        }

        nameTextView.text = goal.name
        amountTextView.text = amountText
        totalTextView.text = totalText
        minusTextView.text = minusText
        notesTextView.text = notesText ?: "-"

        val layoutParams = rangeThumbView.layoutParams as LinearLayout.LayoutParams

        layoutParams.weight = (goal.amount / goal.total) * 100

        rangeThumbView.layoutParams = layoutParams
    }
}