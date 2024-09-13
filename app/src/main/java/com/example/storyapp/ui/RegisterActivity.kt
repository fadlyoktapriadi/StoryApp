package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.model.AuthViewModel
import com.example.storyapp.repository.ViewModelFactory
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.storyapp.repository.Result

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        binding.btnRegister.setOnClickListener {
            register()
        }
    }


    private fun register() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        viewModel.register(name, email, password).observe(this) { account ->
            when (account) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Akun dengan berhasil dibuat.")
                        setPositiveButton("Lanjut") { _, _ ->
                            directToLogin()
                        }
                        create()
                        show()
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Akun gagal dibuat.")
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

    private fun directToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgRegister, View.TRANSLATION_X, -20f, 20f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val header =
            ObjectAnimator.ofFloat(binding.txHeaderRegister, View.ALPHA, 1f).setDuration(200)
        val desc = ObjectAnimator.ofFloat(binding.txDescRegister, View.ALPHA, 1f).setDuration(200)
        val txregister =
            ObjectAnimator.ofFloat(binding.txNamaRegister, View.ALPHA, 1f).setDuration(200)
        val edregister =
            ObjectAnimator.ofFloat(binding.edtNamaRegisterLayout, View.ALPHA, 1f).setDuration(200)
        val txemail =
            ObjectAnimator.ofFloat(binding.txRegisterEmail, View.ALPHA, 1f).setDuration(200)
        val edemail =
            ObjectAnimator.ofFloat(binding.edtEmailRegisterLayout, View.ALPHA, 1f).setDuration(200)
        val txpassword =
            ObjectAnimator.ofFloat(binding.txRegisterPassword, View.ALPHA, 1f).setDuration(200)
        val edpassword =
            ObjectAnimator.ofFloat(binding.edtPasswordLayout, View.ALPHA, 1f).setDuration(200)
        val btnregister =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                header,
                desc,
                txregister,
                edregister,
                txemail,
                edemail,
                txpassword,
                edpassword,
                btnregister
            )
            start()
        }
    }
}