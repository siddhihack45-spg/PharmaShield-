package com.example.pharmashield

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object OcrHelper {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun processImageUri(
        context: Context,
        imageUri: Uri,
        onSuccess: (extractedText: String) -> Unit,
        onFailure: (exception: Exception) -> Unit
    ) {
        try {
            val inputImage = InputImage.fromFilePath(context, imageUri)
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    onSuccess(visionText.text)
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}