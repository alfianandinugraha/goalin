package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.goalin.model.Goal
import com.example.goalin.ui.BackView
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
    private lateinit var addTransactionButton: Button

    private var amount: Float = 0F
    private var total: Float = 0F

    companion object {
        const val CHANGED = 1
    }

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

        val backView = findViewById<BackView>(R.id.back_view)
        val notesTextView = findViewById<TextView>(R.id.notes)

        addTransactionButton = findViewById<Button>(R.id.add_transaction)
        val editButton = findViewById<ButtonView>(R.id.edit_btn)
        val editGoalActivity = Intent(this, EditGoalActivity::class.java)
        val addTransactionActivity = Intent(this, AddTransactionActivity::class.java)

        val mainActivityIntent = Intent(this, MainActivity::class.java)

        editButton.setOnClickListener {
            startActivity(editGoalActivity)
        }

        backView.setOnClickListener {
            val newGoal = goal.copy(
                amount = amount,
                total = total
            )
            val isChanged = !goal.equals(newGoal)

            if (isChanged) {
                mainActivityIntent.putExtra("goal", Gson().toJson(newGoal))
                setResult(CHANGED, mainActivityIntent)
            }

            finish()
        }

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                AddTransactionActivity.SUCCESS -> {
                    val input = it.data?.getStringExtra("input")
                    val goal = Gson().fromJson(input, Goal::class.java)

                    amount += goal.amount
                    total += goal.total
                    updateNominal()
                    updateEnabledButton()
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
        updateEnabledButton()
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

    private fun updateEnabledButton() {
        addTransactionButton.isEnabled = amount < total
    }
}