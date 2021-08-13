package com.bonhams.expensemanagement.data.services

import com.bonhams.expensemanagement.BuildConfig
import com.bonhams.expensemanagement.data.model.LoginResponse
import com.bonhams.expensemanagement.data.services.requests.LoginRequest
import com.bonhams.expensemanagement.utils.RetrofitHeaderInterceptor
import com.bonhams.expensemanagement.utils.RetrofitStatusInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object RetrofitBuilder {
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    )
        .addInterceptor(RetrofitHeaderInterceptor())
        .addInterceptor(RetrofitStatusInterceptor())
        .build()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build() //Doesn't require the adapter
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}

interface ApiService {

    @POST("deliveryboy/login/")
    suspend fun loginUser(@Body loginRequest: LoginRequest): LoginResponse
}