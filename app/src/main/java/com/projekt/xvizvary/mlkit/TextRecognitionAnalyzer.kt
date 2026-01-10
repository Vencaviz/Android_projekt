package com.projekt.xvizvary.mlkit

import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/**
 * CameraX ImageAnalyzer that uses ML Kit for text recognition
 */
class TextRecognitionAnalyzer(
    private val onTextRecognized: (String) -> Unit,
    private val onError: (Exception) -> Unit
) : ImageAnalysis.Analyzer {

    private val textRecognizer: TextRecognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    private var isProcessing = false
    private var lastProcessedTime = 0L
    private val processingIntervalMs = 500L // Process every 500ms to avoid overload

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        
        // Throttle processing
        if (isProcessing || (currentTime - lastProcessedTime) < processingIntervalMs) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        isProcessing = true
        lastProcessedTime = currentTime

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val recognizedText = visionText.text
                if (recognizedText.isNotBlank()) {
                    onTextRecognized(recognizedText)
                }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
            .addOnCompleteListener {
                isProcessing = false
                imageProxy.close()
            }
    }

    fun close() {
        textRecognizer.close()
    }
}

/**
 * Data class for recognized text with bounding box
 */
data class RecognizedTextBlock(
    val text: String,
    val boundingBox: Rect?
)
