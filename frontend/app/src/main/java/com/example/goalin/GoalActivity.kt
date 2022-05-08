package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.goalin.model.Goal
import com.example.goalin.util.format.Currency
import com.example.goalin.ui.ButtonView
import com.google.gson.Gson
import kotlin.math.abs

class GoalActivity : AppCompatActivity() {
    private lateinit var nameTextView: TextView
    private lateinit var amountTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var minusTextView: TextView
    private lateinit var rangeThumbView: View

    private var amount: Float = 0F
    private var total: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val goalJSON = intent.getStringExtra("goal")

        val goal = Gson().fromJson(goalJSON, Goal::class.java)

        amount = goal.amount
        total = goal.total
        val notesText = goal.notes

        nameTextView = findViewById(R.id.name)
        amountTextView = findViewById(R.id.amount)
        totalTextView = findViewById(R.id.total)
        minusTextView = findViewById(R.id.minus)
        rangeThumbView = findViewById(R.id.range_thumb)

        val notesTextView = findViewById<TextView>(R.id.notes)

        val editButton = findViewById<ButtonView>(R.id.edit_btn)
        val addTransactionButton = findViewById<Button>(R.id.add_transaction)
        val editGoalActivity = Intent(this, EditGoalActivity::class.java)
        val addTransactionActivity = Intent(this, AddTransactionActivity::class.java)

        editButton.setOnClickListener {
            startActivity(editGoalActivity)
        }

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                AddTransactionActivity.SUCCESS -> {
                    val input = it.data?.getStringExtra("input")
                    val goal = Gson().fromJson(input, Goal::class.java)

                    amount += goal.amount
                    total += goal.total
                    updateNominal()
                }
            }
        }

        addTransactionButton.setOnClickListener {
            addTransactionActivity.putExtra("goal", goalJSON)
            startForResult.launch(addTransactionActivity)
        }

        nameTextView.text = goal.name
        notesTextView.text = notesText ?: "-"
        updateNominal()
    }

    private fun updateNominal() {
        val amountText = Currency.rupiah(amount)
        val totalText = Currency.rupiah(total)
        val minusText = "Tersisa ${Currency.rupiah(abs(amount - total))} lagi"

        amountTextView.text = amountText
        totalTextView.text = totalText
        minusTextView.text = minusText

        val layoutParams = rangeThumbView.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = (amount / total) * 100
        rangeThumbView.layoutParams = layoutParams
    }
}