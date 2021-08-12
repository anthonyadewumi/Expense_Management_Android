package com.bonhams.expensemanagement.utils

import okhttp3.Interceptor
import okhttp3.Response


class RetrofitStatusInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val response = proceed(
            request()
                .newBuilder()
                .addHeader("Authorization", "Bearer ")
                .build()
        )

        if (response.code() == 500) {

            return response
        }

        return response
    }
}
