package com.example.pharmashield

data class InteractionHazard(
    val riskLevel: String, // CRITICAL, MODERATE, SAFE
    val title: String,
    val mechanism: String,
    val recommendation: String
)

object InteractionMatrix {

    private val rules = listOf(
        Pair("Warfarin", "Aspirin") to InteractionHazard(
            riskLevel = "CRITICAL",
            title = "Severe Bleeding Hazard",
            mechanism = "Both Warfarin and Aspirin inhibit blood clotting mechanisms. Synergistic co-administration exponentially increases internal bleeding risk.",
            recommendation = "Do NOT co-administer without urgent physician monitoring."
        ),
        Pair("Warfarin", "Ibuprofen") to InteractionHazard(
            riskLevel = "CRITICAL",
            title = "GI Hemorrhage & Anticoagulant Amplification",
            mechanism = "NSAIDs cause gastric mucosal erosion and impair platelet activation, severely accentuating Warfarin anticoagulant potency.",
            recommendation = "Substitute Ibuprofen with Acetaminophen for mild pain control."
        ),
        Pair("Metformin", "Lisinopril") to InteractionHazard(
            riskLevel = "MODERATE",
            title = "Hypoglycemia & Renal Monitoring",
            mechanism = "ACE inhibitors may increase insulin sensitivity, amplifying Metformin hypoglycemic effects.",
            recommendation = "Monitor blood glucose levels regularly during initial co-therapy."
        )
    )

    fun evaluate(selectedMeds: List<String>, conditions: List<String>): List<InteractionHazard> {
        val hazards = mutableListOf<InteractionHazard>()

        for (i in selectedMeds.indices) {
            for (j in i + 1 until selectedMeds.size) {
                val m1 = selectedMeds[i]
                val m2 = selectedMeds[j]

                val match = rules.find { (pair, _) ->
                    (pair.first.equals(m1, true) && pair.second.equals(m2, true)) ||
                            (pair.first.equals(m2, true) && pair.second.equals(m1, true))
                }

                match?.let { hazards.add(it.second) }
            }
        }

        if (conditions.contains("Kidney Impairment") && selectedMeds.any { it.equals("Ibuprofen", true) }) {
            hazards.add(
                InteractionHazard(
                    riskLevel = "CRITICAL",
                    title = "Renal Toxicity Warning (Ibuprofen + Kidney Impairment)",
                    mechanism = "NSAIDs inhibit renal prostaglandins, compromising glomerular filtration rate in pre-existing renal disease.",
                    recommendation = "Avoid NSAIDs in kidney disease. Consult nephrologist."
                )
            )
        }

        if (conditions.contains("Pregnancy") && selectedMeds.any { it.equals("Warfarin", true) || it.equals("Lisinopril", true) }) {
            hazards.add(
                InteractionHazard(
                    riskLevel = "CRITICAL",
                    title = "Teratogenic Risk Warning (Pregnancy Contraindication)",
                    mechanism = "Warfarin and ACE inhibitors cause severe fetal abnormalities and dysgenesis.",
                    recommendation = "Strictly contraindicated in pregnancy."
                )
            )
        }

        return hazards
    }
}
