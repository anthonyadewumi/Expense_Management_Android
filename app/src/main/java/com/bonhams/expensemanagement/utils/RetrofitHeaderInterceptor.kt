package com.bonhams.expensemanagement.utils

import okhttp3.Interceptor
import okhttp3.Response

class RetrofitHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader("Authorization", "Bearer ")
                .build()
        )
    }
}
