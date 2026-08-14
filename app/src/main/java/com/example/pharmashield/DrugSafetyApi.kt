package com.example.pharmashield

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Response Data Models for OpenFDA API
data class FdaRecallResponse(
    val results: List<FdaRecallResult>?
)

data class FdaRecallResult(
    val product_description: String?,
    val reason_for_recall: String?,
    val status: String?,
    val recall_initiation_date: String?
)

interface DrugSafetyService {
    @GET("drug/enforcement.json")
    suspend fun checkDrugRecall(
        @Query("search") query: String,
        @Query("limit") limit: Int = 1
    ): FdaRecallResponse
}

object ApiClient {
    private const val BASE_URL = "https://api.fda.gov/"

    val service: DrugSafetyService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DrugSafetyService::class.java)
    }
}