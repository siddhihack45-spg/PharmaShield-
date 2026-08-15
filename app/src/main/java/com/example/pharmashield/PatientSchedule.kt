package com.example.pharmashield

data class DoseItem(
    val id: Long,
    val name: String,
    val timeSlot: String,
    var isCompleted: Boolean,
    val daysUntilExp: Int
)

object PatientSchedule {
    val initialDoses = mutableListOf(
        DoseItem(1, "Amoxicillin Trihydrate 500mg", "Morning (08:00 AM)", true, 420),
        DoseItem(2, "Lipitor (Atorvastatin 20mg)", "Noon (01:00 PM)", false, 14),
        DoseItem(3, "Metformin HCl 500mg", "Evening (07:00 PM)", false, 240),
        DoseItem(4, "Lisinopril 10mg", "Night (10:00 PM)", false, 5)
    )

    fun calculateAdherence(doses: List<DoseItem>): Int {
        if (doses.isEmpty()) return 0
        val completed = doses.count { it.isCompleted }
        return ((completed.toDouble() / doses.size) * 100).toInt()
    }
}
