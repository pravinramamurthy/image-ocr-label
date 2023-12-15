package com.util

import android.graphics.Bitmap
import android.util.Log
import com.example.alleassingment.ui.fragments.ImageSelectionScreen
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/**
 * Created by Praveen on 14,December,2023
 */
object RecognizeText {
    fun getTextFromImage(bitmap: Bitmap, listener: TextRecognitionListener) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val recognizedText = visionText.text
                Log.d(ImageSelectionScreen.TAG, "recognizeText: $recognizedText")
                if (recognizedText.isNotEmpty()) {
                    listener.onTextRecognized(recognizedText)
                } else {
                    // Handle empty recognized text if needed
                    listener.onTextRecognitionFailed("Recognized text is empty.")
                }
            }
            .addOnFailureListener { e ->
                Log.d(ImageSelectionScreen.TAG, "recognizeText: ${"Text recognition failed: $e"}")
                return@addOnFailureListener
            }
    }
}

interface TextRecognitionListener {
    fun onTextRecognized(text: String)
    fun onTextRecognitionFailed(error: String)
}