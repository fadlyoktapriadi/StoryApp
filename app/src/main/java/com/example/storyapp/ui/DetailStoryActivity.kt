package com.example.storyapp.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_story)
        supportActionBar?.hide()

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

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

        val image = intent.extras?.getString(StoryAdapter.IMAGE_STORY).toString()
        val title = intent.extras?.getString(StoryAdapter.TITLE_STORY).toString()
        val desc = intent.extras?.getString(StoryAdapter.DESC_STORY).toString()

        Glide.with(this)
            .load(image)
            .into(binding.imageView)

        binding.titleDetail.text = title
        binding.txDesc.text = desc
    }
}