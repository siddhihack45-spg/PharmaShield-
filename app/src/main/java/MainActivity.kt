private val selectImageLauncher = registerForActivityResult(
    ActivityResultContracts.GetContent()
) { uri: Uri? ->
    uri?.let { processPrescriptionImage(it) }
}

// Inside onCreate:
galleryButton.setOnClickListener {
    selectImageLauncher.launch("image/*")
}

private fun processPrescriptionImage(imageUri: Uri) {
    resultTextView.text = "Scanning prescription with ML Kit..."

    OcrHelper.processImageUri(
        context = this,
        imageUri = imageUri,
        onSuccess = { rawExtractedText ->
            if (rawExtractedText.isBlank()) {
                resultTextView.text = "No readable text found in prescription image."
                return@processImageUri
            }

            val firstWord = rawExtractedText.lines()
                .firstOrNull { it.isNotBlank() }
                ?.split(" ")
                ?.firstOrNull() ?: rawExtractedText

            verifyDrugSafety(
                drugName = firstWord,
                dosage = "Extracted from image",
                rawText = rawExtractedText
            )
        },
        onFailure = { e ->
            Toast.makeText(this, "OCR Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            resultTextView.text = "Failed to process image."
        }
    )
}