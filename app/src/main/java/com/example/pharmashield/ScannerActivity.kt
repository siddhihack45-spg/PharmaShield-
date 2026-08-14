package com.example.pharmashield

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.util.Size
import android.widget.Button
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

class ScannerActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var graphicOverlay: GraphicOverlay
    private lateinit var captureButton: Button
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    @Volatile
    private var latestDetectedText: String = ""

    companion object {
        private const val TAG = "ScannerActivity_OCR"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        previewView = findViewById(R.id.previewView)
        graphicOverlay = findViewById(R.id.graphicOverlay)
        captureButton = findViewById(R.id.captureButton)

        captureButton.setOnClickListener {
            if (latestDetectedText.isNotBlank()) {
                triggerHapticFeedback()
                val resultIntent = Intent().apply {
                    putExtra("SCANNED_TEXT", latestDetectedText)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "No text detected yet. Keep aiming at text.", Toast.LENGTH_SHORT).show()
            }
        }

        startCamera()
    }

    private fun triggerHapticFeedback() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            @OptIn(ExperimentalGetImage::class)
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(Size(720, 1280))
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        graphicOverlay.setPreviewSize(imageProxy.width, imageProxy.height)

                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            recognizer.process(image)
                                .addOnSuccessListener { visionText ->
                                    latestDetectedText = visionText.text

                                    val boundingBoxes = mutableListOf<Rect>()
                                    for (block in visionText.textBlocks) {
                                        block.boundingBox?.let { box ->
                                            boundingBoxes.add(box)
                                        }
                                    }
                                    graphicOverlay.updateTextRects(boundingBoxes)
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "OCR Processing failed: ${e.message}", e)
                                    graphicOverlay.updateTextRects(emptyList())
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e(TAG, "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}