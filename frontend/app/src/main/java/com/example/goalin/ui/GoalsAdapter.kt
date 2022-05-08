package com.example.goalin.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goalin.GoalActivity
import com.example.goalin.R
import com.example.goalin.model.Goal
import com.example.goalin.util.format.Currency
import com.google.gson.Gson

class GoalsAdapter(
    private val context: Context
): RecyclerView.Adapter<GoalsAdapter.Holder>() {
    lateinit var goals: MutableList<Goal>

    class Holder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val amount: TextView = view.findViewById(R.id.amount)
        val total: TextView = view.findViewById(R.id.total)
        val container: LinearLayout = view.findViewById(R.id.goal_item)
    }

    private val goalActivityIntent = Intent(context, GoalActivity::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.view_goal_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val goal = goals[position]
        val amountText = Currency.rupiah(goal.amount)
        val totalText = Currency.rupiah(goal.total)

        holder.name.text = goal.name
        holder.amount.text = amountText
        holder.total.text = totalText

        holder.container.setOnClickListener {
            goalActivityIntent.putExtra("goal", Gson().toJson(goal))
            context.startActivity(goalActivityIntent)
        }
    }

    override fun getItemCount(): Int = goals.size


}