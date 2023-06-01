package com.example.submissionappstory.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.submissionappstory.R
import com.example.submissionappstory.data.local.pagedir.Token
import com.example.submissionappstory.data.local.pagedir.TokenPreferences
import com.example.submissionappstory.ui.util.showToast

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var authPreferences: TokenPreferences
    private lateinit var token: Token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authPreferences = TokenPreferences(this)
        token = Token(authPreferences)

        Handler(Looper.getMainLooper()).postDelayed({ token() }, DELAY)
    }

    private fun token() {
        token.getToken().observe(this) { key ->
            if (key != null) {
                if (!key.equals(NOT_FOUND)) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                showToast(this, getString(R.string.error))
            }
        }
    }

    companion object {
        const val NOT_FOUND = "NotFound"
        const val DELAY : Long = 1500
    }
}