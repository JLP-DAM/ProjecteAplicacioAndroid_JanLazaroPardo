package com.gilded.services

import com.gilded.models.Receipt
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReceiptService {

    @GET("/gilded/receipts")
    suspend fun getReceipts(): Response<List<Receipt>>

    @POST("/gilded/receipts")
    suspend fun postReceipt(@Body receipt: Receipt): Receipt

    @DELETE("/gilded/receipts/{id}")
    suspend fun deleteReceipt(@Path("id") id: Long)

    @PUT("/gilded/receipts")
    suspend fun updateReceipt(@Body receipt: Receipt)
}