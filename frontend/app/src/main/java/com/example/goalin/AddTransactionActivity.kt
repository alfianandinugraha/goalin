package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.goalin.model.Goal
import com.example.goalin.model.ResponseStatus
import com.example.goalin.service.TransactionService
import com.example.goalin.service.WalletService
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.DatePickerView
import com.example.goalin.ui.EditTextView
import com.example.goalin.ui.SelectView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTransactionActivity : AppCompatActivity() {
    private val scope = CoroutineScope(CoroutineName("AddTransactionScope") + Dispatchers.IO)

    companion object {
        const val SUCCESS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val goalJSON = intent.getStringExtra("goal")
        val goal = Gson().fromJson(goalJSON, Goal::class.java)

        val saveButton = findViewById<ButtonView>(R.id.save_btn)
        val walletSelectView = findViewById<SelectView<String>>(R.id.wallet_select_view)
        val nameEditTextView = findViewById<EditTextView>(R.id.name_edit_text_view)
        val amountEditTextView = findViewById<EditTextView>(R.id.amount_edit_text_view)
        val datePicker = findViewById<DatePickerView>(R.id.date_picker)

        val transactionService = TransactionService(this)
        val walletService = WalletService(application)

        nameEditTextView.setText(goal.name)

        scope.launch {
            walletService.getAll()
        }

        saveButton.setOnClickListener {
            val amount = amountEditTextView.text.toString().toFloatOrNull()
            val walletId = walletSelectView.selectedOption?.value
            val createdAt = datePicker.calendar?.timeInMillis

            if (amount == null || walletId == null || createdAt == null) {
                Toast
                    .makeText(this, "Harap isi semua bidang diatas", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            scope.launch {
                val body = TransactionService.CreateTransactionBodyRequest(
                    amount = amount,
                    walletId = walletId,
                    createdAt = createdAt,
                    goalId = goal.id
                )
                try {
                    val response = transactionService.store(body)

                    withContext(Dispatchers.Main) {
                        Toast
                            .makeText(this@AddTransactionActivity, "Berhasil menambahkan Transaksi", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@AddTransactionActivity, GoalActivity::class.java)
                        intent.putExtra("input", Gson().toJson(body))
                        setResult(SUCCESS, intent)
                        finish()
                    }
                } catch (err: ApiResponseException) {
                    withContext(Dispatchers.Main) {
                        Toast
                            .makeText(this@AddTransactionActivity, err.response().message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            walletService.walletsFlow.collect {
                when(it) {
                    is ResponseStatus.Loading -> {

                    }
                    is ResponseStatus.Success -> {
                        saveButton.isEnabled = true
                        walletSelectView.options = it.payload.map { wallet ->
                            SelectView.Option(wallet.name, wallet.id)
                        }
                    }
                    is ResponseStatus.Error -> {
                        Toast
                            .makeText(this@AddTransactionActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
        }
    }
}