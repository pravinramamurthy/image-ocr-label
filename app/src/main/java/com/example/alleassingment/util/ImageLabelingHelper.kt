package com.example.alleassingment.util

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageLabelingHelper {

    private val imageLabeler: ImageLabeler by lazy {
        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.7f)
            .build()

        ImageLabeling.getClient(options)
    }

    fun labelImage(bitmap: Bitmap, onLabelsReceived: (List<String>) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)

        imageLabeler.process(image)
            .addOnSuccessListener { labels ->
                val labelList = labels.map { label ->
                    label.text
                }
                onLabelsReceived(labelList)
            }
            .addOnFailureListener { e ->
                Log.e("ImageLabeling", "Labeling failed: $e")
                onLabelsReceived(emptyList())
            }
    }
}
