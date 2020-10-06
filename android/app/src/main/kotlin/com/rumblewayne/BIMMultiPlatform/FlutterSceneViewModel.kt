package com.rumblewayne.BIMMultiPlatform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumblewayne.bimmultiplatform.db.cache.Database
import kotlinx.coroutines.flow.MutableStateFlow

class FlutterSceneViewModel(private val database: Database): ViewModel() {
    var colorFlow = MutableStateFlow<String>("#FFFFFF")

    init {
        viewModelScope
        listenToBackgroundColorFlow()
    }

    private fun listenToBackgroundColorFlow() {
        database.backgroundColorFlow.watch {
            colorFlow.value = it.hex
        }
    }
}