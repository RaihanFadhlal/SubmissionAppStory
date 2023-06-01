package com.example.submissionappstory.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.submissionappstory.R
import com.example.submissionappstory.data.local.pagedir.TokenPreferences
import com.example.submissionappstory.data.local.repository.AccountRepository
import com.example.submissionappstory.databinding.ActivityRegisBinding
import com.example.submissionappstory.ui.factory.ViewModelFactory
import com.example.submissionappstory.ui.main.LoginActivity.Companion.emailPattern
import com.example.submissionappstory.ui.util.setSafeOnClickListener
import com.example.submissionappstory.ui.util.showLoading
import com.example.submissionappstory.ui.util.showToast
import com.example.submissionappstory.ui.viewmodel.LogResViewModel

class RegisActivity : AppCompatActivity() {

    private lateinit var regisBinding: ActivityRegisBinding
    private lateinit var tokenPref: TokenPreferences
    private lateinit var accountRepo: AccountRepository
    private lateinit var logResViewModel: LogResViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        regisBinding = ActivityRegisBinding.inflate(layoutInflater)
        setContentView(regisBinding.root)

        supportActionBar?.hide()

        tokenPref = TokenPreferences(this)
        accountRepo = AccountRepository()
        logResViewModel = ViewModelProvider(
            this,
            ViewModelFactory(tokenPref, accountRepo, this)
        ) [LogResViewModel::class.java]

        regisBinding.btnRegister.setSafeOnClickListener { userRegister() }
        regisBinding.btnLogin.setOnClickListener { finish() }

        setAnimation()
    }

    private fun userRegister() {
        val username = regisBinding.edtUsername.text.toString().trim()
        val email = regisBinding.edtEmailInput.text.toString().trim()
        val password = regisBinding.edtPasswordInput.text.toString().trim()

        logResViewModel.isLoading.observe(this) { isLoading ->
            showLoading(regisBinding.progressBar, isLoading)
        }

        when {
            username.isEmpty() -> {
                regisBinding.edtUsername.error = getString(R.string.must_filled)
            }
            email.isEmpty() -> {
                regisBinding.edtEmailInput.error = getString(R.string.must_filled)
            }
            password.isEmpty() -> {
                regisBinding.edtPasswordInput.error = getString(R.string.must_filled)
            }
            password.length < 8 -> {
                regisBinding.edtPasswordInput.error = getString(R.string.must_filled)
            }
            !email.matches(emailPattern) -> {
                regisBinding.edtEmailInput.error = getString(R.string.must_filled)
            }
            else -> {
                logResViewModel.register(username, email, password)
                logResViewModel.regMessage.observe(this) {
                    it.getContentIfNotHandled()?.let {
                        showToast(this, getString(R.string.email_taken))
                    }
                }
                logResViewModel.registerUser.observe(this) { register ->
                    if (register != null) {
                        finish()
                        showToast(this, getString(R.string.succeed))
                    }
                }
            }
        }
    }

    private fun setAnimation() {
        val title = ObjectAnimator.ofFloat(regisBinding.tvRegister, View.ALPHA, 1f).setDuration(300)
        val registerImage = ObjectAnimator.ofFloat(regisBinding.imgRegisterImage, View.ALPHA, 1f).setDuration(300)
        val name = ObjectAnimator.ofFloat(regisBinding.username, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(regisBinding.email, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(regisBinding.password, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(regisBinding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val btnRegister = ObjectAnimator.ofFloat(regisBinding.btnRegister, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(name, email, password)
        }
        val togetherSecond = AnimatorSet().apply {
            playTogether(btnLogin, btnRegister)
        }
        AnimatorSet().apply {
            playSequentially(title, registerImage, together, togetherSecond)
            start()
        }
    }
}