package com.example.submissionappstory.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.submissionappstory.R
import com.example.submissionappstory.data.local.pagedir.Token
import com.example.submissionappstory.data.local.pagedir.TokenPreferences
import com.example.submissionappstory.data.local.repository.AccountRepository
import com.example.submissionappstory.databinding.ActivityLoginBinding
import com.example.submissionappstory.ui.factory.ViewModelFactory
import com.example.submissionappstory.ui.util.setSafeOnClickListener
import com.example.submissionappstory.ui.util.showLoading
import com.example.submissionappstory.ui.util.showToast
import com.example.submissionappstory.ui.viewmodel.LogResViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenPref: TokenPreferences
    private lateinit var logResViewModel: LogResViewModel
    private lateinit var token: Token
    private lateinit var accountRepo: AccountRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        tokenPref = TokenPreferences(this)
        token = Token(tokenPref)
        accountRepo = AccountRepository()
        logResViewModel = ViewModelProvider(
            this,
            ViewModelFactory(tokenPref, accountRepo, this)
        ) [LogResViewModel::class.java]

        binding.btnLogin.setSafeOnClickListener { userLogin() }
        binding.btnRegister.setSafeOnClickListener {
            startActivity(Intent(this, RegisActivity::class.java))
        }

        animation()
    }

    private fun userLogin() {
        val email = binding.edtEmailInput.text.toString().trim()
        val password = binding.edtPasswordInput.text.toString().trim()

        logResViewModel.isLoading.observe(this) { isLoading ->
            showLoading(binding.progressBar, isLoading)
        }
        when {
            email.isEmpty() -> {
                binding.edtEmailInput.error = getString(R.string.must_filled)
            }
            password.isEmpty() -> {
                binding.edtPasswordInput.error = getString(R.string.must_filled)
            }
            password.length < 8 -> {
                binding.edtPasswordInput.error = getString(R.string.min_password)
            }
            !email.matches(emailPattern) -> {
                binding.edtEmailInput.error = getString(R.string.wrong_format)
            }
            else -> {
                logResViewModel.login(email, password)
                logResViewModel.logMessage.observe(this) {
                    it.getContentIfNotHandled()?.let {
                        showToast(this, getString(R.string.login_failed))
                    }
                }
                logResViewModel.loginUser.observe(this) { login ->
                    token.setToken(login.token)
                    startActivity(Intent(this, MainActivity::class.java))
                    showToast(this, "${getString(R.string.succeed)} ${login.name}")
                }
            }
        }
    }

    private fun animation() {
        val appTitle = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(300)
        val loginImage = ObjectAnimator.ofFloat(binding.imgLoginImage, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(300)
        val loginButton = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val registerButton = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(email, password)
        }
        val togetherSecond = AnimatorSet().apply {
            playTogether(loginButton, registerButton)
        }
        AnimatorSet().apply {
            playSequentially(appTitle, loginImage, together, togetherSecond)
            start()
        }
    }

    companion object{
        val emailPattern = Regex("[a-zA-Z\\d._]+@[a-z]+\\.+[a-z]+")
    }

}