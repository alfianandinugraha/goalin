package com.example.goalin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.goalin.service.WalletService
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.ui.ButtonView
import com.example.goalin.ui.SelectView
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTransactionActivity : AppCompatActivity() {
    private val scope = CoroutineScope(CoroutineName("AddTransactionScope") + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val saveButton = findViewById<ButtonView>(R.id.save_btn)
        val walletSelectView = findViewById<SelectView<String>>(R.id.wallet_select_view)

        val walletService = WalletService(this)

        scope.launch {
            try {
                val response = walletService.getAll()
                withContext(Dispatchers.Main) {
                    walletSelectView.options = response.payload.map {
                        SelectView.Option(it.name, it.id)
                    }
                }
            } catch (err: ApiResponseException) {
                withContext(Dispatchers.Main) {
                    Toast
                        .makeText(this@AddTransactionActivity, err.response().message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        saveButton.setOnClickListener {
            finish()
        }
    }
}