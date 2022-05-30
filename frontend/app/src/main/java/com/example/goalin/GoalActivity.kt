package com.example.goalin

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalin.model.Goal
import com.example.goalin.model.ResponseStatus
import com.example.goalin.service.GoalService
import com.example.goalin.service.TransactionService
import com.example.goalin.ui.BackView
import com.example.goalin.util.format.Currency
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.TransactionAdapter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private var amountDelete: Float = 0F

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
        val listTransactionRecyclerView = findViewById<RecyclerView>(R.id.list_transactions)

        val editGoalActivity = Intent(this, EditGoalActivity::class.java)
        val addTransactionActivity = Intent(this, AddTransactionActivity::class.java)

        val mainActivityIntent = Intent(this, MainActivity::class.java)

        val goalService = ViewModelProvider(this).get(GoalService::class.java)
        val transactionService = ViewModelProvider(this).get(TransactionService::class.java)

        val transactionAdapter = TransactionAdapter(this)

        transactionAdapter.setOnClickDeleteListener = {
            AlertDialog.Builder(this)
                .setTitle("Hapus Transaksi")
                .setMessage("Apakah kamu yakin menghapus transaksi dengan jumlah Rp${it.amount}?")
                .setPositiveButton("Hapus Transaksi", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, id: Int) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            transactionService.delete(it.goalId, it.id)
                            amountDelete = it.amount
                        }
                    }

                })
                .setNegativeButton("Batal", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, id: Int) {}
                })
                .show()
        }

        editButton.setOnClickListener {
            startActivity(editGoalActivity)
        }

        deleteGoalButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus Goal")
                .setMessage("Apakah kamu yakin menghapus goal?")
                .setPositiveButton("Hapus", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, id: Int) {
                        lifecycleScope.launch {
                            goalService.delete(goal.id)
                        }
                    }

                })
                .setNegativeButton("Batal", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, id: Int) {}
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
                    lifecycleScope.launch(Dispatchers.IO) {
                        goalService.getDetail(goal.id)
                    }
                    lifecycleScope.launch(Dispatchers.IO) {
                        transactionService.getAll(goal.id)
                    }
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

        lifecycleScope.launch(Dispatchers.IO) {
            transactionService.getAll(goal.id)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            goalService.deleteFlow.collect {
                when (it) {
                    is ResponseStatus.Loading -> {
                        Toast
                            .makeText(this@GoalActivity, "Menghapus...", Toast.LENGTH_LONG)
                            .show()
                    }
                    is ResponseStatus.Success -> {
                        Toast
                            .makeText(this@GoalActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                        setResult(DELETED, mainActivityIntent)
                        finish()
                    }
                    is ResponseStatus.Error -> {
                        Toast
                            .makeText(this@GoalActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            goalService.getDetailFlow.collect {
                when (it) {
                    is ResponseStatus.Loading -> {
                    }
                    is ResponseStatus.Success -> {
                        amount = it.payload.amount
                        total = it.payload.total
                        updateNominal()
                        updateEnabledButton()
                    }
                    is ResponseStatus.Error -> {
                        Toast
                            .makeText(this@GoalActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            transactionService.getAllFlow.collect {
                when(it) {
                    is ResponseStatus.Loading -> {}
                    is ResponseStatus.Success -> {
                        listTransactionRecyclerView.layoutManager = LinearLayoutManager(this@GoalActivity)
                        transactionAdapter.transactions = it.payload.toMutableList()
                        listTransactionRecyclerView.adapter = transactionAdapter
                    }
                    is ResponseStatus.Error -> {}
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            transactionService.deleteFlow.collect {
                when(it) {
                    is ResponseStatus.Loading -> {}
                    is ResponseStatus.Success -> {
                        transactionService.getAll(goal.id)

                        amount -= amountDelete

                        updateNominal()
                        updateEnabledButton()

                        Toast
                            .makeText(this@GoalActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    is ResponseStatus.Error -> {
                        amountDelete = 0F
                        Toast
                            .makeText(this@GoalActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
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