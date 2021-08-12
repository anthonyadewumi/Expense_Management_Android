package com.bonhams.expensemanagement.utils

import okhttp3.Interceptor
import okhttp3.Response

class RetrofitHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
//                .addHeader("appid", "hello")
                .addHeader("Authorization", "Bearer ")
//                .removeHeader("User-Agent")
//                .addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0")
                .build()
        )
    }
}
