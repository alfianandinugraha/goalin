package com.example.goalin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goalin.fragments.GoalItemFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, GoalItemFragment())
            .commit()
    }
}