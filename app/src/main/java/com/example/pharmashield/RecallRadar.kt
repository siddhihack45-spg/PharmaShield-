package com.example.pharmashield

data class RecallItem(
    val drugName: String,
    val batchNo: String,
    val agency: String,
    val date: String,
    val severity: String,
    val reason: String,
    val action: String
)

data class PharmacyItem(
    val name: String,
    val distance: String,
    val address: String,
    val phone: String,
    val rating: Double,
    val status: String
)

object RecallRadar {
    val recallDataset = listOf(
        RecallItem(
            drugName = "Zantac (Ranitidine HCl 150mg)",
            batchNo = "BATCH-RNT-404",
            agency = "FDA / EMA",
            date = "2026-07-28",
            severity = "SEVERE",
            reason = "Trace contamination of NDMA nitrosamine impurity exceeding safety threshold.",
            action = "Quarantine inventory & return to dispensing pharmacy."
        ),
        RecallItem(
            drugName = "Valsartan 160mg Film-Coated Tablets",
            batchNo = "VAL-8891-B",
            agency = "CDSCO",
            date = "2026-06-14",
            severity = "SEVERE",
            reason = "Potential cross-contamination during active ingredient crystallization.",
            action = "Discontinue use immediately under physician advice."
        ),
        RecallItem(
            drugName = "Children Pain Relief Oral Liquid",
            batchNo = "CPR-2025-11",
            agency = "WHO Alert",
            date = "2026-05-02",
            severity = "MODERATE",
            reason = "Dosage cup marking calibration variance in specific batch lot.",
            action = "Use standardized syringe for precise dosage measurement."
        )
    )

    val pharmacies = listOf(
        PharmacyItem("ShieldCare Central Verified Pharmacy", "0.4 km", "42 Medical Enclave, Health City", "+18005550199", 4.9, "Open 24/7 • Authenticity Verified Node"),
        PharmacyItem("Apollo Bio-Shield Chemist", "1.2 km", "88 Cyber Park Avenue, West Wing", "+18005550842", 4.8, "Open until 11:00 PM • Verified Scanner"),
        PharmacyItem("MediLife Authentic Drug Store", "2.5 km", "104 Metro Plaza, Block C", "+18005550311", 4.7, "Open until 10:00 PM • Cold-Chain Verified")
    )
}
