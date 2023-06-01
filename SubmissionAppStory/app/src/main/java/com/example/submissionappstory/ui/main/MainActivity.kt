package com.example.submissionappstory.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submissionappstory.R
import com.example.submissionappstory.data.local.pagedir.Token
import com.example.submissionappstory.data.local.pagedir.TokenPreferences
import com.example.submissionappstory.data.local.repository.AccountRepository
import com.example.submissionappstory.data.local.room.StoryDb
import com.example.submissionappstory.databinding.ActivityMainBinding
import com.example.submissionappstory.ui.adapter.LoadingAdapter
import com.example.submissionappstory.ui.adapter.StoryAdapter
import com.example.submissionappstory.ui.factory.ViewModelFactory
import com.example.submissionappstory.ui.main.MapsActivity.Companion.LIST_LOCATION
import com.example.submissionappstory.ui.main.MapsActivity.Companion.LIST_USERNAME
import com.example.submissionappstory.ui.util.setSafeOnClickListener
import com.example.submissionappstory.ui.util.showLoading
import com.example.submissionappstory.ui.viewmodel.LogResViewModel
import com.example.submissionappstory.ui.viewmodel.MainViewModel
import com.example.submissionappstory.ui.viewmodel.PageModel
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pageModel: PageModel
    private lateinit var tokenPref: TokenPreferences
    private lateinit var accountRepo: AccountRepository
    private lateinit var logResViewModel: LogResViewModel
    private lateinit var token: Token
    private lateinit var storyDb: StoryDb
    private lateinit var storyAdapter: StoryAdapter

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(tokenPref, accountRepo, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(true)

        storyDb = StoryDb.getInstance(this)
        storyAdapter = StoryAdapter()
        tokenPref = TokenPreferences(this)
        accountRepo = AccountRepository()
        token = Token(tokenPref)

        logResViewModel = ViewModelProvider(
            this,
            ViewModelFactory(tokenPref, accountRepo, this)
        )[LogResViewModel::class.java]

        pageModel = ViewModelProvider(
            this,
            ViewModelFactory(tokenPref, accountRepo, this)
        )[PageModel::class.java]

        token.getToken().observe(this) { token ->
            if (token != null) {
                pageAdapter()
                mainViewModel.getStory().observe(this) { story ->
                    storyAdapter.submitData(lifecycle, story)
                    storyAdapter.snapshot().items
                }
                mainViewModel.loading.value = false
            } else {
                Log.e(TOKEN, INVALID_TOKEN)
                finishAffinity()
            }
        }

        mainViewModel.loading.observe(this) { isLoading ->
            showLoading(binding.progressBar, isLoading)
        }

        binding.refresh.setOnRefreshListener { refreshPage() }
        binding.fabUpload.setSafeOnClickListener {
            startActivity(Intent(this, NewStoryActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private var listLocation: ArrayList<LatLng>? = null
    private var listUserName: ArrayList<String>? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.location -> {
                startActivity(Intent(this, MapsActivity::class.java)
                    .also {
                        it.putExtra(LIST_LOCATION, listLocation)
                        it.putExtra(LIST_USERNAME, listUserName)
                    }
                )
            }
            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.logout_confirm))
                builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    this.getSharedPreferences(DATA, 0)
                        .edit().clear().apply()
                    val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .also {
                            token.deleteToken()
                            startActivity(it)
                        }
                    finishAffinity()
                }
                val alert = builder.create()
                alert.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pageAdapter() {
        val layout = LinearLayoutManager(this@MainActivity)
        val itemDecoration = DividerItemDecoration(this@MainActivity, layout.orientation)

        storyAdapter = StoryAdapter()
        storyAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.rvStory.smoothScrollToPosition(0)
                }
            }
        })
        binding.rvStory.apply {
            layoutManager = layout
            smoothScrollToPosition(0)
            addItemDecoration(itemDecoration)
            adapter =
                storyAdapter.withLoadStateFooter(footer = LoadingAdapter { storyAdapter.retry() })
        }
    }

    private fun refreshPage() {
        binding.refresh.isRefreshing = true
        Timer().schedule(1000) {
            binding.refresh.isRefreshing = false
        }
        binding.rvStory.smoothScrollBy(0, 0)
        startActivity(Intent(this, MainActivity::class.java))
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        finishAffinity()
    }

    companion object{
        const val TOKEN = "token"
        const val INVALID_TOKEN = "invalid token"
        const val DATA = "data"
    }
}