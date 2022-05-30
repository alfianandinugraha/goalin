package com.example.goalin.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goalin.R
import com.example.goalin.model.Goal
import com.example.goalin.model.Transaction
import com.example.goalin.util.constant.Month
import com.example.goalin.util.format.Currency
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(val context: Context): RecyclerView.Adapter<TransactionAdapter.Holder>() {
    var transactions: MutableList<Transaction> = mutableListOf()

    lateinit var current: Transaction

    var setOnClickDeleteListener: ((transaction: Transaction) -> Unit)? = null;

    class Holder(view: View): RecyclerView.ViewHolder(view) {
        val amount: TextView = view.findViewById(R.id.amount)
        val date: TextView = view.findViewById(R.id.date)
        val deleteBtn: ImageView = view.findViewById(R.id.delete_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.view_transaction, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val transaction = transactions[position]
        val amountText = Currency.rupiah(transaction.amount)
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(transaction.createdAt)!!

        val day = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val dateText = "$day ${Month().items[month]} $year"

        holder.amount.text = amountText
        holder.date.text = dateText

        holder.deleteBtn.setOnClickListener {
            current = transaction
            setOnClickDeleteListener?.invoke(transaction)
        }
    }

    override fun getItemCount(): Int = transactions.size
}