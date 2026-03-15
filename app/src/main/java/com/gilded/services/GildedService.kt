package com.gilded.services

import com.gilded.models.Receipt
import com.gilded.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GildedService {

    @GET("/receipts")
    suspend fun getReceipts(): Response<List<Receipt>>

    @POST("/receipts")
    suspend fun postReceipt(@Body receipt: Receipt): Receipt

    @DELETE("/receipts/{id}")
    suspend fun deleteReceipt(@Path("id") id: Long)

    @PUT("/receipts")
    suspend fun updateReceipt(@Body receipt: Receipt)

    @GET("/users/{email}/{password}")
    suspend fun getUser(@Path("email") email: String, @Path("password") password: String): Response<User?>

    @POST("/users")
    suspend fun postUser(@Body user: User): User?
}