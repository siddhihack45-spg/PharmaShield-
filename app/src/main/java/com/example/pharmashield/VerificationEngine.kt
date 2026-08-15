package com.example.pharmashield

data class ProvenanceStep(
    val stepName: String,
    val location: String,
    val timestamp: String,
    val isCompleted: Boolean
)

data class VerificationResult(
    val status: String, // GENUINE, FAKE, RECALLED
    val safetyScore: Double,
    val drugName: String,
    val dosage: String,
    val manufacturer: String,
    val batchNo: String,
    val gtin: String,
    val mfd: String,
    val exp: String,
    val labChecksum: String,
    val provenance: List<ProvenanceStep>,
    val warnings: String
)

object VerificationEngine {

    val sampleSerials = mapOf(
        "AUTHENTIC-AZ-9942" to VerificationResult(
            status = "GENUINE",
            safetyScore = 99.8,
            drugName = "Lipitor (Atorvastatin Calcium)",
            dosage = "20 mg",
            manufacturer = "Pfizer BioPharma Ltd.",
            batchNo = "PF-2026-9942",
            gtin = "00300450123459",
            mfd = "2026-02-10",
            exp = "2028-02-10",
            labChecksum = "0x8f9a2b7d4e1c3f6a9b8c7d6e5f4a3b2c",
            provenance = listOf(
                ProvenanceStep("Bio-Pharma Lab Production", "Freiburg, Germany", "Feb 10, 2026 04:30 UTC", true),
                ProvenanceStep("EU Regulatory Compliance Check", "Frankfurt Quality Hub", "Feb 12, 2026 11:15 UTC", true),
                ProvenanceStep("Cryptographic Cold-Chain Transit", "Air Cargo Flight LH-842", "Feb 15, 2026 18:00 UTC", true),
                ProvenanceStep("Pharmacy Inventory Scan", "Central Med Depot", "Feb 18, 2026 09:20 UTC", true)
            ),
            warnings = "Store below 25°C. Keep container tightly closed."
        ),

        "AUTHENTIC-AM-7721" to VerificationResult(
            status = "GENUINE",
            safetyScore = 100.0,
            drugName = "Amoxil (Amoxicillin Trihydrate)",
            dosage = "500 mg",
            manufacturer = "GSK Pharmaceuticals",
            batchNo = "GSK-2026-7721",
            gtin = "00300450987654",
            mfd = "2026-04-01",
            exp = "2027-10-01",
            labChecksum = "0x1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d",
            provenance = listOf(
                ProvenanceStep("Formulation & Blister Sealing", "Ware, UK", "Apr 01, 2026 08:00 UTC", true),
                ProvenanceStep("Batch Spectrometry Testing", "GSK QC Division", "Apr 03, 2026 14:20 UTC", true),
                ProvenanceStep("Global Distribution Transit", "Heathrow Logistics", "Apr 05, 2026 06:10 UTC", true),
                ProvenanceStep("Pharmacy Inventory Scan", "Apollo Med Shield", "Apr 08, 2026 10:00 UTC", true)
            ),
            warnings = "Complete full prescribed course."
        ),

        "FAKE-COUNTERFEIT-108" to VerificationResult(
            status = "FAKE",
            safetyScore = 12.4,
            drugName = "Suspected Counterfeit Prescription",
            dosage = "Unknown / Starch Fillers",
            manufacturer = "Unregistered Black-Market Operation",
            batchNo = "FAKE-108-CRIMINAL",
            gtin = "00000000000000",
            mfd = "Invalid Stamp",
            exp = "Invalid Stamp",
            labChecksum = "INVALID_HASH_FAILED_HMAC",
            provenance = listOf(
                ProvenanceStep("Illicit Operation Flagged", "Unidentified Location", "ALERT TRIGGERED", false),
                ProvenanceStep("Regulatory Check Failed", "Interpol Database", "BLOCKED", false)
            ),
            warnings = "CRITICAL ALERT: DO NOT CONSUME! Contact Poison Control Immediately."
        ),

        "RECALLED-BATCH-404" to VerificationResult(
            status = "RECALLED",
            safetyScore = 45.0,
            drugName = "Zantac (Ranitidine HCl)",
            dosage = "150 mg",
            manufacturer = "Sanofi Health Ltd.",
            batchNo = "BATCH-RNT-404",
            gtin = "00300450555666",
            mfd = "2025-08-12",
            exp = "2027-08-12",
            labChecksum = "0x777888999aaabbbcccdddeeefff00011",
            provenance = listOf(
                ProvenanceStep("Batch Manufacturing", "Packaging Unit 4", "Aug 12, 2025", true),
                ProvenanceStep("FDA Safety Recall Issued", "FDA Safety Alert Portal", "RECALLED", false)
            ),
            warnings = "RECALL NOTICE: Impurity NDMA detected above acceptable limits. Return to dispensing pharmacy."
        )
    )

    fun verifyCode(code: String): VerificationResult {
        val cleanCode = code.trim().uppercase()
        return sampleSerials[cleanCode] ?: VerificationResult(
            status = if (cleanCode.contains("FAKE")) "FAKE" else if (cleanCode.contains("RECALL")) "RECALLED" else "GENUINE",
            safetyScore = 98.6,
            drugName = "Verified Bio-Pharma Compound ($cleanCode)",
            dosage = "Standard Formulated Dosage",
            manufacturer = "Certified Global Pharma Manufacturer",
            batchNo = "BATCH-$cleanCode",
            gtin = "00300450" + (100000..999999).random(),
            mfd = "2026-01-15",
            exp = "2028-01-15",
            labChecksum = "0x" + (1..32).map { "0123456789abcdef".random() }.joinToString(""),
            provenance = listOf(
                ProvenanceStep("Bio-Pharma Production", "Verified Plant", "Jan 15, 2026", true),
                ProvenanceStep("Quality Check", "QC Portal", "Jan 18, 2026", true),
                ProvenanceStep("Cryptographic Transit", "Logistics", "Jan 22, 2026", true),
                ProvenanceStep("Pharmacy Scan", "Verified Station", "Today", true)
            ),
            warnings = "Keep out of reach of children. Store in a cool dry place."
        )
    }
}
