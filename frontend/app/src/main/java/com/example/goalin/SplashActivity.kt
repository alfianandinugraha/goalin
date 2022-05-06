package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goalin.service.TokenService
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val scope = CoroutineScope(CoroutineName("SplashScope") + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val mainActivity = Intent(this, MainActivity::class.java)
        val loginActivity = Intent(this, LoginActivity::class.java)
        val tokenService = TokenService(this)
        val token = tokenService.get()
        val activity = if (token !== null) mainActivity else loginActivity

        scope.launch {
            delay(1500)
            startActivity(activity)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}