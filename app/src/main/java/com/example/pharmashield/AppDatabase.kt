package com.example.pharmashield

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [BannedDrug::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bannedDrugDao(): BannedDrugDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pharma_shield_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialBannedList(database.bannedDrugDao())
                    }
                }
            }

            suspend fun populateInitialBannedList(dao: BannedDrugDao) {
                val initialCdscoList = listOf(
                    BannedDrug(
                        drugName = "Nimesulide + Paracetamol",
                        category = "NSAID / Analgesic FDC",
                        banReason = "Hepatotoxicity risks & safety concerns in pediatric/adult combinations.",
                        banYear = "2011"
                    ),
                    BannedDrug(
                        drugName = "Analgin",
                        category = "Analgesic",
                        banReason = "Risk of agranulocytosis and severe bone marrow toxicity.",
                        banYear = "2013"
                    ),
                    BannedDrug(
                        drugName = "Cisapride",
                        category = "Gastrointestinal",
                        banReason = "Risk of cardiac arrhythmias and QT interval prolongation.",
                        banYear = "2011"
                    ),
                    BannedDrug(
                        drugName = "Piperazine",
                        category = "Anthelminthic",
                        banReason = "Neurotoxicity and hypersensitivity risks.",
                        banYear = "2018"
                    )
                )
                dao.insertAll(initialCdscoList)
            }
        }
    }
}