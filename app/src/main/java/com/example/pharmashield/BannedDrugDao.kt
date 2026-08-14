package com.example.pharmashield

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BannedDrugDao {

    @Query("SELECT * FROM cdsco_banned_drugs WHERE LOWER(drugName) LIKE '%' || LOWER(:query) || '%' LIMIT 1")
    suspend fun checkBannedDrugExact(query: String): BannedDrug?

    @Query("SELECT * FROM cdsco_banned_drugs")
    suspend fun getAllBannedDrugs(): List<BannedDrug>

    @Query("SELECT COUNT(*) FROM cdsco_banned_drugs")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(drugs: List<BannedDrug>)
}