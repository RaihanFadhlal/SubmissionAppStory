package com.example.submissionappstory.viewmodeltest

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.submissionappstory.data.local.pagedir.TokenPreferences
import com.example.submissionappstory.data.local.repository.AccountRepository
import com.example.submissionappstory.data.remote.apiresponse.LoginResponse
import com.example.submissionappstory.data.remote.apiresponse.RegisResponse
import com.example.submissionappstory.datatest.DummyData
import com.example.submissionappstory.datatest.MainDispatcher
import com.example.submissionappstory.datatest.getOrAwait
import com.example.submissionappstory.ui.viewmodel.LogResViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LogResVMTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcher()

    @Mock
    private lateinit var accountRepo: AccountRepository
    private lateinit var tokenPref: TokenPreferences
    private lateinit var logresviewModel: LogResViewModel

    private val dummyLogin = DummyData.loginResult()
    private val dummyRegister = DummyData.register()
    private val dummyEmail = "raihanaziz@gmail.com"
    private val dummyPass = "raihan123"

    @Before
    fun setupTest() {
        tokenPref = TokenPreferences(Mockito.mock(Context::class.java))
        logresviewModel = LogResViewModel(accountRepo)
    }


    @Test
    fun loginSuccess() = runTest {
        val expectedData = MutableLiveData<LoginResponse>()
        expectedData.value = dummyLogin
        Mockito.`when`(accountRepo.login(dummyEmail, dummyPass)).thenReturn(expectedData)

        val actualData = logresviewModel.login(dummyEmail, dummyPass).getOrAwait()
        Mockito.verify(accountRepo).login(dummyEmail, dummyPass)
        Assert.assertNotNull(actualData)
        assertEquals(expectedData.value, actualData)
    }

    @Test
    fun registerSuccess() = runTest {
        val expectedData = MutableLiveData<RegisResponse>()
        expectedData.value = dummyRegister
        Mockito.`when`(accountRepo.register("dummytest", dummyEmail, dummyPass)).thenReturn(expectedData)

        val actualData = logresviewModel.register("dummytest",dummyEmail, dummyPass).getOrAwait()
        Mockito.verify(accountRepo).register("dummytest", dummyEmail, dummyPass)
        Assert.assertNotNull(actualData)
        assertEquals(expectedData.value, actualData)
    }
}