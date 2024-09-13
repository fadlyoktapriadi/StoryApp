package com.example.storyapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.di.reduceFileImage
import com.example.storyapp.di.uriToFile
import com.example.storyapp.model.AddStoryViewModel
import com.example.storyapp.repository.Result
import com.example.storyapp.repository.ViewModelFactory
import com.example.storyapp.ui.CameraActivity.Companion.CAMERAX_RESULT
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager

    private val requestPermissionLauncherFileX =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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
        supportActionBar?.title = "Add New Story"

        if (!allPermissionsGranted()) {
            requestPermissionLauncherFileX.launch(REQUIRED_PERMISSION)
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setupAction() {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showToast("Foto belum dipilih")
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    uploadImage()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    uploadImage()
                }

                else -> {
                    Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.descStory.text.toString()

            if (binding.setlokasi.isChecked) {
                if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val lat = location.latitude
                            val lon = location.longitude

                            viewModel.getSession().observe(this) { user ->
                                if (user.isLogin) {
                                    viewModel.addStoryWithLocation(
                                        user.token,
                                        imageFile,
                                        description,
                                        lat.toFloat(),
                                        lon.toFloat()
                                    )
                                        .observe(this) { result ->
                                            when (result) {
                                                is Result.Loading -> {
                                                    binding.progressIndicator.visibility =
                                                        View.VISIBLE
                                                }

                                                is Result.Success -> {
                                                    binding.progressIndicator.visibility = View.GONE
                                                    AlertDialog.Builder(this).apply {
                                                        setTitle("Yeah!")
                                                        setMessage("Story berhasil ditambahkan.")
                                                        setPositiveButton("Lanjut") { _, _ ->
                                                            directToMain()
                                                        }
                                                        create()
                                                        show()
                                                    }
                                                }

                                                is Result.Error -> {
                                                    binding.progressIndicator.visibility = View.GONE
                                                    Toast.makeText(
                                                        this,
                                                        "Gagal diupload",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                } else {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            } else {
                viewModel.getSession().observe(this) { user ->
                    if (user.isLogin) {
                        viewModel.addStory(user.token, imageFile, description)
                            .observe(this) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        binding.progressIndicator.visibility = View.VISIBLE
                                    }

                                    is Result.Success -> {
                                        binding.progressIndicator.visibility = View.GONE
                                        AlertDialog.Builder(this).apply {
                                            setTitle("Yeah!")
                                            setMessage("Story berhasil ditambahkan.")
                                            setPositiveButton("Lanjut") { _, _ ->
                                                directToMain()
                                            }
                                            create()
                                            show()
                                        }
                                    }

                                    is Result.Error -> {
                                        binding.progressIndicator.visibility = View.GONE
                                        Toast.makeText(
                                            this,
                                            "Gagal diupload",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                    }
                }
            }


        } ?: showToast(getString(R.string.empty_image_warning))
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun directToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}