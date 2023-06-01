package com.example.submissionappstory.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submissionappstory.data.local.pagedir.TokenPreferences
import com.example.submissionappstory.data.local.repository.AccountRepository
import com.example.submissionappstory.di.Injection
import com.example.submissionappstory.ui.viewmodel.*

class ViewModelFactory(
    private val tokenPref: TokenPreferences,
    private val accountRepo: AccountRepository,
    private val context: Context,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogResViewModel::class.java)) {
            return LogResViewModel(accountRepo) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

            return MainViewModel(Injection.storyRepo(tokenPref, context)) as T
        }
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(Injection.mapsRepo()) as T
        }
        if (modelClass.isAssignableFrom(PageModel::class.java)) {
            return PageModel(Injection.storyRepo(tokenPref, context)) as T
        }
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(Injection.uploadRepo()) as T
        }
        throw IllegalArgumentException("error ${modelClass.name}")
    }
}