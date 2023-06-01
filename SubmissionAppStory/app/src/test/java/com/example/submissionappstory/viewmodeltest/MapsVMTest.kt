package com.example.submissionappstory.viewmodeltest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.submissionappstory.data.local.repository.MapsRepository
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import com.example.submissionappstory.datatest.DummyData
import com.example.submissionappstory.datatest.getOrAwait
import com.example.submissionappstory.ui.viewmodel.MapsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsVMTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: MapsRepository
    private lateinit var viewModel: MapsViewModel
    private val dummy = DummyData.dummyStory()

    @Before
    fun setup() {
        viewModel = MapsViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getStoryWithMapsReturnSuccess() = runTest {
        val expectedData = MutableLiveData<List<ListStory>>()
        expectedData.postValue(dummy)
        Mockito.`when`(repository.getStoryLocation("token")).thenReturn(expectedData)

        val actualData = viewModel.getStoryLocation("token").getOrAwait()
        Mockito.verify(repository).getStoryLocation("token")

        Assert.assertNotNull(actualData)
        Assert.assertEquals(actualData, expectedData.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getMarkerMapsReturnSuccess() = runTest {
        val expectedData = MutableLiveData<List<ListStory>>()
        expectedData.postValue(dummy)
        Mockito.`when`(repository.getStory()).thenReturn(expectedData)

        val actualData = viewModel.getStory().getOrAwait()
        Mockito.verify(repository).getStory()

        Assert.assertNotNull(actualData)
        Assert.assertEquals(actualData, expectedData.value)
    }
}