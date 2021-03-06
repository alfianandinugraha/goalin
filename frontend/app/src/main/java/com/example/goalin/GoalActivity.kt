package com.example.goalin

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.goalin.model.Goal
import com.example.goalin.service.GoalService
import com.example.goalin.ui.BackView
import com.example.goalin.util.format.Currency
import com.example.goalin.ui.ButtonView
import com.example.goalin.util.http.ApiResponseException
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val scope = CoroutineScope(CoroutineName("GoalScope") + Dispatchers.IO)

    companion object {
        const val CHANGED = 1
        const val DELETED = 2
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
        val deleteGoalButton = findViewById<ImageView>(R.id.delete_goal_btn)
        val editButton = findViewById<ButtonView>(R.id.edit_btn)
        val editGoalActivity = Intent(this, EditGoalActivity::class.java)
        val addTransactionActivity = Intent(this, AddTransactionActivity::class.java)

        val mainActivityIntent = Intent(this, MainActivity::class.java)

        val goalService = GoalService(this)

        editButton.setOnClickListener {
            startActivity(editGoalActivity)
        }

        deleteGoalButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus Goal")
                .setMessage("Apakah kamu yakin menghapus goal?")
                .setPositiveButton("Hapus", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, id: Int) {
                        scope.launch {
                            try {
                                goalService.delete(goal.id)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@GoalActivity,
                                        "Goal berhasil dihapus",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mainActivityIntent.putExtra("goal", Gson().toJson(goal))
                                    setResult(DELETED, mainActivityIntent)
                                    finish()
                                }
                            } catch (err: ApiResponseException) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@GoalActivity,
                                        err.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                })
                .setNegativeButton("Batal", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, id: Int) {

                    }

                })
                .show()
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