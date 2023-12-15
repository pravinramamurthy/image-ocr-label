package com.example.alleassingment.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Created by Praveen on 14,December,2023
 */
class ImageDetailsViewModel : ViewModel() {

    var screenData: ImageDetails? = null
    var selectedBitmap: Bitmap? = null

    fun updateBitmap(bitmap: Bitmap?) {
        selectedBitmap = bitmap
    }

    fun updateCollectionsList(data: List<String>) {
        screenData?.labels = data
    }

    fun updateScreenData(date: ImageDetails) {
        screenData = date
    }

    private val _screenDataState = Channel<ImageDetails>(1)
    val screenDataState get() = _screenDataState.receiveAsFlow()
    fun sendScreenData(value: ImageDetails) {
        viewModelScope.launch {
            _screenDataState.send(value)
        }
    }

    private val _collectionData = Channel<List<String>>(1)
    val collectionData get() = _collectionData.receiveAsFlow()
    fun sendData(value: List<String>) {
        viewModelScope.launch {
            _collectionData.send(value)
        }
    }
}

data class ImageDetails(
    var note: String,
    var labels: List<String>,
    var description: String
)
