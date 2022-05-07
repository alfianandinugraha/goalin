package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goalin.service.TokenService
import com.example.goalin.service.UserService
import com.example.goalin.util.http.ApiResponseException
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

        val userService = UserService(this)

        scope.launch {
            delay(1000)

            if (token == null) {
                startActivity(loginActivity)
                return@launch
            }

            try {
                userService.get()
                startActivity(mainActivity)
            } catch (err: ApiResponseException) {
                startActivity(loginActivity)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}