package com.example.pharmashield

data class PillItem(
    val name: String,
    val imprint: String,
    val shape: String,
    val color: String,
    val dosage: String,
    val category: String,
    val manufacturer: String
)

object PillMatcher {
    val pillDatabase = listOf(
        PillItem("Atorvastatin (Lipitor)", "PD 156", "oval", "white", "20 mg", "Cholesterol Lowering", "Pfizer"),
        PillItem("Amoxicillin Trihydrate", "GSK 500", "capsule", "cyan", "500 mg", "Antibiotic", "GSK"),
        PillItem("Metformin HCl", "M 500", "round", "white", "500 mg", "Type 2 Diabetes", "Mylan"),
        PillItem("Warfarin Sodium", "TARO 2", "round", "red", "2 mg", "Blood Thinner", "Taro"),
        PillItem("Lisinopril", "LU 10", "oval", "yellow", "10 mg", "Blood Pressure", "Lupin"),
        PillItem("Ibuprofen", "I-2", "round", "red", "200 mg", "NSAID Pain Reliever", "Advil")
    )

    fun filterPills(shape: String, color: String, imprintQuery: String): List<PillItem> {
        val cleanImprint = imprintQuery.trim().lowercase()
        return pillDatabase.filter { pill ->
            val matchShape = shape.equals("all", ignoreCase = true) || pill.shape.equals(shape, ignoreCase = true)
            val matchColor = color.equals("all", ignoreCase = true) || pill.color.equals(color, ignoreCase = true)
            val matchImprint = cleanImprint.isEmpty() ||
                    pill.imprint.lowercase().contains(cleanImprint) ||
                    pill.name.lowercase().contains(cleanImprint)
            matchShape && matchColor && matchImprint
        }
    }
}
