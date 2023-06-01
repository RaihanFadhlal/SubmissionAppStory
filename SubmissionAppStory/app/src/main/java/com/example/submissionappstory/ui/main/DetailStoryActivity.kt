package com.example.submissionappstory.ui.main

import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.submissionappstory.R
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import com.example.submissionappstory.databinding.ActivityDetailStoryBinding
import com.example.submissionappstory.ui.adapter.StoryAdapter.Companion.DETAIL
import com.example.submissionappstory.ui.util.withDateFormat
import java.util.*

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.story_detail)

        @Suppress("DEPRECATION")
        val detail = intent.getParcelableExtra<ListStory>(DETAIL) as ListStory
        val geocoder = Geocoder(this, Locale.getDefault())
        val address = detail.lat?.let { latitude ->
            detail.lon?.let { longitude ->
                geocoder.getFromLocation(latitude, longitude, 1)
            }
        }
        val cityName = address?.get(0)?.subAdminArea
        val stateName = address?.get(0)?.adminArea
        val countryName = address?.get(0)?.countryName
        val addressName = "$cityName, $stateName, $countryName"

        binding.apply {
            tvNameDetail.text = detail.name
            tvDetailDescription.text = detail.description
            tvCreatedDetail.text = detail.createdAt.withDateFormat()
            Glide.with(this@DetailStoryActivity)
                .load(detail.photoUrl)
                .transform(CenterInside(), RoundedCorners(25))
                .into(ivDetailPhoto)
            if (address != null) {
                tvLocation.text = addressName
            } else {
                tvLocation.isVisible = false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}