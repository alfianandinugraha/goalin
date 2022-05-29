package com.example.goalin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.goalin.model.ResponseStatus
import com.example.goalin.service.TokenService
import com.example.goalin.service.UserService
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

        val userService = ViewModelProvider(this).get(UserService::class.java)

        lifecycleScope.launch {
            delay(1000)

            if (token == null) {
                startActivity(loginActivity)
                return@launch
            }

            userService.getDetail()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            userService.getDetailFlow.collect {
                when(it) {
                    is ResponseStatus.Loading -> {}
                    is ResponseStatus.Success -> {
                        startActivity(mainActivity)
                    }
                    is ResponseStatus.Error -> {
                        startActivity(loginActivity)
                    }
                }
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