package com.bonhams.expensemanagement.data.services

import com.bonhams.expensemanagement.BuildConfig
import com.bonhams.expensemanagement.data.services.requests.*
import com.bonhams.expensemanagement.data.services.responses.*
import com.bonhams.expensemanagement.utils.RetrofitHeaderInterceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


object RetrofitBuilder {
    private const val API_BASE_URL = "https://maps.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).addInterceptor(RetrofitHeaderInterceptor())
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build() //Doesn't require the adapter
    }
    private fun getRetrofitMatrix(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build() //Doesn't require the adapter
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
    val googleApiService: GoogleApiService = getRetrofitMatrix().create(GoogleApiService::class.java)
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

    @POST("admin/delete-claim")
    suspend fun deleteClaim(@Body deleteClaimRequest: DeleteClaimRequest): CommonResponse

    @Multipart
    @POST("upload-claim-attachment")
    suspend fun uploadImage(@Part  claimImage :List<MultipartBody.Part>): CommonResponse

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

    @GET("my_profile")
    suspend fun profileDetail(): MyProfileResponse
}
interface GoogleApiService {
    @GET("maps/api/distancematrix/json")
    suspend fun getDistanceInfo(@QueryMap parameters: MutableMap<String, String>): DistanceMatrixResponse

}