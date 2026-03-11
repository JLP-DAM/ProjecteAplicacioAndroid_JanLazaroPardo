package com.gilded.services

import com.gilded.models.Receipt
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface ReceiptService {

    @GET("/gilded/receipts")
    suspend fun getReceipts(): Response<List<Receipt>>

    @POST("/gilded/receipts")
    suspend fun postReceipt(receipt: Receipt)
}