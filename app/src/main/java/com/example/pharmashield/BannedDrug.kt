package com.example.pharmashield

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cdsco_banned_drugs")
data class BannedDrug(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val drugName: String,
    val category: String,
    val banReason: String,
    val banYear: String
)