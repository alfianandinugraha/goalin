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
    private lateinit var notesTextView: TextView

    private var amount: Float = 0F
        set(value) {
            amountTextView.text = Currency.rupiah(value)
            field = value
        }
    private var total: Float = 0F
        set(value) {
            totalTextView.text = Currency.rupiah(value)
            field = value
        }
    private var name: String = ""
        set(value) {
            nameTextView.text = value
            field = value
        }
    private var notes: String? = ""
        set(value) {
            notesTextView.text = value
            field = value
        }

    private var amountDelete: Float = 0F

    companion object {
        const val CHANGED = 1
        const val DELETED = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        nameTextView = findViewById(R.id.name)
        amountTextView = findViewById(R.id.amount)
        totalTextView = findViewById(R.id.total)
        minusTextView = findViewById(R.id.minus)
        rangeThumbView = findViewById(R.id.range_thumb)
        notesTextView = findViewById(R.id.notes)
        addTransactionButton = findViewById(R.id.add_transaction)

        val goalJSON = intent.getStringExtra("goal")
        val goal = Gson().fromJson(goalJSON, Goal::class.java)
        amount = goal.amount
        total = goal.total
        notes = if(goal.notes.isNullOrEmpty()) "-" else goal.notes
        name = goal.name

        val backView = findViewById<BackView>(R.id.back_view)

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
                total = total,
                name = name,
                notes = notes
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
                EditGoalActivity.SUCCESS -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        goalService.getDetail(goal.id)
                    }
                }
            }
        }

        editButton.setOnClickListener {
            editGoalActivity.putExtra("goal", goalJSON)
            startForResult.launch(editGoalActivity)
        }

        addTransactionButton.setOnClickListener {
            addTransactionActivity.putExtra("goal", goalJSON)
            startForResult.launch(addTransactionActivity)
        }

        updateUI()

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
                        name = it.payload.name
                        notes = it.payload.notes

                        updateUI()
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

                        updateUI()

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

    private fun updateUI() {
        val minusText = "Tersisa ${Currency.rupiah(abs(amount - total))} lagi"

        minusTextView.text = if(amount < total) minusText else "Sudah terpenuhi"

        val layoutParams = rangeThumbView.layoutParams as LinearLayout.LayoutParams
        val weight = (amount / total) * 100
        layoutParams.weight = if (weight > 100) 100F else weight
        rangeThumbView.layoutParams = layoutParams

        addTransactionButton.isEnabled = amount < total
    }
}