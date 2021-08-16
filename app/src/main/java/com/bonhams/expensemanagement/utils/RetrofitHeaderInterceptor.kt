package com.bonhams.expensemanagement.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class RetrofitHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val response = proceed(
            if(AppPreferences.isLoggedIn){
                request()
                    .newBuilder()
//                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer ${AppPreferences.userToken}")
                    .build()
            }
            else {
                request()
                    .newBuilder()
                    .build()
            }
        )

        if (response.code() == 500) {
            Log.e("Retrofit Interceptor", "Response Code: ${response.code()}")
        }
        else if (response.code() == 401) {
            Log.e("Retrofit Interceptor", "Response Code: ${response.code()}")
        }

        Log.d("Interceptor========", "==================================")
        Log.d("Interceptor request", request().toString())
        Log.d("Interceptor response", response.toString())

        return response
    }
}
