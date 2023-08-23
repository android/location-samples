/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.example.sleepsamplekotlin

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.example.sleepsamplekotlin.data.SleepRepository
import com.android.example.sleepsamplekotlin.data.db.SleepClassifyEventEntity
import com.android.example.sleepsamplekotlin.data.db.SleepSegmentEventEntity
import kotlinx.coroutines.launch

class MainViewModel(private val repository: SleepRepository) : ViewModel() {

    val subscribedToSleepDataLiveData = repository.subscribedToSleepDataFlow.asLiveData()

    fun updateSubscribedToSleepData(subscribed: Boolean) = viewModelScope.launch {
        repository.updateSubscribedToSleepData(subscribed)
    }

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSleepSegments: LiveData<List<SleepSegmentEventEntity>> =
        repository.allSleepSegmentEvents.asLiveData()

    val allSleepClassifyEventEntities: LiveData<List<SleepClassifyEventEntity>> =
        repository.allSleepClassifyEvents.asLiveData()
}

class MainViewModelFactory(private val repository: SleepRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
