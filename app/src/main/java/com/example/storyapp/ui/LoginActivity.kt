package com.example.storyapp.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.storyapp.R
import com.example.storyapp.data.preferences.UserModel
import com.example.storyapp.databinding.ActivityLoginBinding
import android.animation.AnimatorSet
import com.example.storyapp.model.AuthViewModel
import com.example.storyapp.repository.Result
import com.example.storyapp.repository.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        viewModel.login(email, password).observe(this) { account ->
            when (account) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    viewModel.saveSession(
                        UserModel(
                            email,
                            "Bearer ${account.data.loginResult?.token!!}"
                        )
                    )
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Login berhasil.")
                        setPositiveButton("Lanjut") { _, _ ->
                            directToMain()
                        }
                        create()
                        show()
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Yah!")
                        setMessage("Login gagal, Username/Password salah.")
                        setPositiveButton("Kembali") { _, _ ->
                        }
                        create()
                        show()
                    }
                }

                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun directToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgRegister, View.TRANSLATION_X, -20f, 20f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val header = ObjectAnimator.ofFloat(binding.txHeaderLogin, View.ALPHA, 1f).setDuration(200)
        val desc = ObjectAnimator.ofFloat(binding.txDescLogin, View.ALPHA, 1f).setDuration(200)
        val txemail = ObjectAnimator.ofFloat(binding.txEmailLogin, View.ALPHA, 1f).setDuration(200)
        val edemail =
            ObjectAnimator.ofFloat(binding.edtEmailLayout, View.ALPHA, 1f).setDuration(200)
        val txpassword =
            ObjectAnimator.ofFloat(binding.txPasswordLogin, View.ALPHA, 1f).setDuration(200)
        val edpassword =
            ObjectAnimator.ofFloat(binding.edtPasswordLayout, View.ALPHA, 1f).setDuration(200)
        val btnlogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(header, desc, txemail, edemail, txpassword, edpassword, btnlogin)
            start()
        }
    }
}