package com.example.submissionappstory.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.submissionappstory.R
import com.example.submissionappstory.data.local.pagedir.Token
import com.example.submissionappstory.data.local.pagedir.TokenPreferences
import com.example.submissionappstory.data.local.repository.AccountRepository
import com.example.submissionappstory.data.local.repository.MapsRepository
import com.example.submissionappstory.databinding.ActivityMapsBinding
import com.example.submissionappstory.ui.factory.ViewModelFactory
import com.example.submissionappstory.ui.viewmodel.LogResViewModel
import com.example.submissionappstory.ui.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var authViewModel: LogResViewModel
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var authRepo: AccountRepository
    private lateinit var mapsRepo: MapsRepository
    private lateinit var token: Token
    private lateinit var mMap: GoogleMap

    private val mapViewModel: MapsViewModel by viewModels {
        ViewModelFactory(tokenPreferences, authRepo, this)
    }

    private var listLocation: ArrayList<LatLng>? = null
    private var listUserName: ArrayList<String>? = null

    private val reqPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getMyLocation()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.location)

        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-1.348994, 115.6112)))

        listLocation = intent.getParcelableArrayListExtra(LIST_LOCATION)
        listUserName = intent.getStringArrayListExtra(LIST_USERNAME)

        mapsModel()
        getMyLocation()
    }

    private fun mapsModel() {
        tokenPreferences = TokenPreferences(this)
        authRepo = AccountRepository()
        mapsRepo = MapsRepository()
        token = Token(tokenPreferences)

        authViewModel = ViewModelProvider(
            this,
            ViewModelFactory(tokenPreferences, authRepo, this)
        )[LogResViewModel::class.java]
        token.getToken().observe(this) { token ->
            if (token != null) {
                mapViewModel.getStoryLocation("Bearer $token")
                mapViewModel.getStory().observe(this) { stories ->
                    stories?.let { story ->
                        for (i in story.listIterator()) {
                            val latLng = LatLng(i.lat!!, i.lon!!)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(i.name)
                                    .snippet(i.description)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            reqPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val LIST_LOCATION = "list_location"
        const val LIST_USERNAME = "list_username"
    }
}