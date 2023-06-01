package com.example.submissionappstory.data.local.pagedir

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.submissionappstory.data.local.pagedir.TokenPreferences.Companion.LOGIN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.authentication: DataStore<Preferences> by preferencesDataStore(name = LOGIN)
class TokenPreferences(private val context: Context) {

    private val key = stringPreferencesKey(LOGINAUTH)

    suspend fun setToken(token: String) {
        context.authentication.edit {
            it[key] = token
        }
    }

    fun getToken(): Flow<String> {
        return  context.authentication.data.map {
            it[key] ?: NOTFOUND
        }
    }

    suspend fun deleteToken() {
        context.authentication.edit {
            it.clear()
        }
    }

    companion object{
       const val LOGIN = "login"
       const val LOGINAUTH = "login_auth"
       const val NOTFOUND = "NotFound"
    }
}