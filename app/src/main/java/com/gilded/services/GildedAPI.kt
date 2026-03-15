package com.gilded.services

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class GildedAPI {
    companion object {
        private var receiptAPI: GildedService? = null

        @Synchronized
        fun API(): GildedService {
            if (receiptAPI == null) {

                val gsonDateFormat = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()

                val unsafeOkHttpClient = getUnsafeOkHttpClient()

                receiptAPI = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gsonDateFormat))
                    .baseUrl("http://10.0.2.2:2006/gilded/")
                    .client(unsafeOkHttpClient)
                    .build()
                    .create(GildedService::class.java)
            }
            return receiptAPI!!
        }

        private fun getUnsafeOkHttpClient(): OkHttpClient {
            try {
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                return OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()

            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}