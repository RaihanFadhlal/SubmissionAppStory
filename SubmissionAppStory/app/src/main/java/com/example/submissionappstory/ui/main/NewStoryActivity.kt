package com.example.submissionappstory.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.submissionappstory.R
import com.example.submissionappstory.data.local.pagedir.Token
import com.example.submissionappstory.data.local.pagedir.TokenPreferences
import com.example.submissionappstory.data.local.repository.AccountRepository
import com.example.submissionappstory.data.local.repository.StoryRepository
import com.example.submissionappstory.databinding.ActivityNewStoryBinding
import com.example.submissionappstory.ui.factory.ViewModelFactory
import com.example.submissionappstory.ui.util.*
import com.example.submissionappstory.ui.viewmodel.LogResViewModel
import com.example.submissionappstory.ui.viewmodel.StoryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding
    private lateinit var tokenPref: TokenPreferences
    private lateinit var accountRepo: AccountRepository
    private lateinit var storyRepo: StoryRepository
    private lateinit var token: Token
    private lateinit var logResViewModel: LogResViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_CODE) {
            if (!allPermissionGranted()) {
                showToast(this, getString(R.string.permission))
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQ_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(tokenPref, accountRepo, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_story)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this, REQ_PERMISSION, REQ_CODE
            )
        }

        tokenPref = TokenPreferences(this)
        accountRepo = AccountRepository()
        storyRepo = StoryRepository()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        token = Token(tokenPref)
        logResViewModel = ViewModelProvider(
            this,
            ViewModelFactory(tokenPref, accountRepo, this)
        )[LogResViewModel::class.java]

        binding.btnGallery.setSafeOnClickListener { openGallery() }
        binding.btnCamera.setSafeOnClickListener { startTakePhoto() }
        binding.btnUpload.setSafeOnClickListener { uploadStory() }
        binding.swLocation.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                location()
            }
        }
    }
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                binding.ivStory.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
            getFile = myFile
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@NewStoryActivity,
                "com.example.submissionappstory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        val chooser = Intent.createChooser(galleryIntent, getString(R.string.select_img))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImage, this)
            getFile = myFile
            binding.ivStory.setImageURI(selectedImage)
        }
    }

    private var location: LatLng? = null
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                location()
            }
        }

    private fun location() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) location = LatLng(loc.latitude, loc.longitude)
            }
        } else {
            showToast(this, getString(R.string.allow_loc))
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun uploadStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val reqImage = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo", file.name, reqImage
            )
            val description =
                binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())

            if (binding.edAddDescription.text.isEmpty()) {
                binding.edAddDescription.error = getString(R.string.must_filled)
            } else {
                token.getToken().observe(this) { token ->
                    if (location != null) {
                        storyViewModel.uploadStory(
                            "Bearer $token", imageMultiPart, description,
                            lat = (location as LatLng).latitude,
                            lon = (location as LatLng).longitude
                        )
                    } else {
                        storyViewModel.uploadStory("Bearer $token", imageMultiPart, description)
                    }
                    storyViewModel.isEnabled.observe(this) { isEnabled ->
                        binding.btnUpload.isEnabled = isEnabled
                    }
                    storyViewModel.storyResponse.observe(this) { response ->
                        if (!response.error) {
                            showToast(this, getString(R.string.upload_success))
                            this.getSharedPreferences("data", 0).edit().clear().apply()

                            val mainIntent = Intent(this, MainActivity::class.java)
                            mainIntent.putExtra(SUCCESS, true)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(mainIntent)
                            finish()
                        } else {
                            showToast(this, getString(R.string.error))
                        }
                    }
                }
            }
        } else showToast(this, getString(R.string.error))
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return true
    }

    companion object {
        const val REQ_CODE = 10
        const val SUCCESS = "upload_success"
        val REQ_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    }
}