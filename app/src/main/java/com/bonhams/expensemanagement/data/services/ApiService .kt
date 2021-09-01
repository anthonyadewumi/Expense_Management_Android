package com.bonhams.expensemanagement.data.services

import com.bonhams.expensemanagement.BuildConfig
import com.bonhams.expensemanagement.data.services.requests.*
import com.bonhams.expensemanagement.data.services.responses.*
import com.bonhams.expensemanagement.utils.RetrofitHeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

object RetrofitBuilder {

    private val okHttpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).addInterceptor(RetrofitHeaderInterceptor())
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

    @POST("login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): LoginResponse

    @POST("forget-pass")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest) : CommonResponse

    @POST("reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest) : CommonResponse

    @POST("claim-list")
    suspend fun claimsList(@Body claimListRequest: ClaimsRequest) : ClaimsResponse

    @POST("create-claim")
    suspend fun createNewClaim(@Body newClaimRequest: NewClaimRequest) : CommonResponse

    @POST("mileage_list")
    suspend fun mileageList(@Body mileageExpenseRequest: MileageExpenseRequest) : MileageListResponse

    @POST("new_mileageclaim")
    suspend fun createNewMileageClaim(@Body mileageClaimRequest: NewMileageClaimRequest) : CommonResponse

    @POST("changePassword")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest) : CommonResponse

    @GET("dropdown")
    suspend fun dropdownData(): DropdownResponse

    @GET("logOut")
    suspend fun logoutUser(): CommonResponse
}