package com.bonhams.expensemanagement.data.services

import com.bonhams.expensemanagement.BuildConfig
import com.bonhams.expensemanagement.data.services.requests.*
import com.bonhams.expensemanagement.data.services.responses.*
import com.bonhams.expensemanagement.utils.RetrofitHeaderInterceptor
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object RetrofitBuilder {
    private const val API_BASE_URL = "https://maps.googleapis.com/"

    /*private val spec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
        .supportsTlsExtensions(true)
        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
        .cipherSuites(
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
            CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
            CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA
        )
        .build()*/
    var spec: ConnectionSpec? = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_2)
        .cipherSuites(
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
        )
        .build()
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).addInterceptor(RetrofitHeaderInterceptor())
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        //.connectionSpecs(Collections.singletonList(spec))

        /*  .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT,
              ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                  .allEnabledTlsVersions()
                  .allEnabledCipherSuites()
                  .build()))*/

        .build()


       // .connectionSpecs(Collections.singletonList(ConnectionSpec.MODERN_TLS))


    private fun getRetrofit(): Retrofit {
        val clint=getUnsafeOkHttpClient()
            .addInterceptor(RetrofitHeaderInterceptor())
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
        clint.addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clint.build())
            .build() //Doesn't require the adapter
    }
    private fun getRetrofitMatrix(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build() //Doesn't require the adapter
    }
    fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
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

    @POST("request_list")
    suspend fun financeRequestList(@Body claimListRequest: ClaimsRequest) : AcceptRequestResponse

    @POST("create-claim")
    suspend fun createNewClaim(@Body newClaimRequest: NewClaimRequest) : CommonResponse

    @POST("edit-expense-claim")
    suspend fun editClaim(@Body newClaimRequest: JsonObject) : CommonResponse

    @POST("notification_list ")
    suspend fun getnoticationData(@Body request: JsonObject) : NotificationListResponse

    @POST("batch-list")
    suspend fun getBatchData(@Body request: JsonObject) : BatchListResponse

    @POST("delete-batch")
    suspend fun deleteBatch(@Body request: JsonObject) : CommonResponse

    @POST("submit-batch")
    suspend fun submitBatch(@Body request: JsonObject) : CommonResponse

    @POST("delete-claim")
    suspend fun deleteClaim(@Body deleteClaimRequest: DeleteClaimRequest): CommonResponse

    @POST("send-reminder")
    suspend fun sendReminder(@Body claimId: JsonObject): CommonResponse

    @POST("claim-detail")
    suspend fun getDetails(@Body claimId: JsonObject): ClaimDetailsResponse

    @POST("claim-detail")
    suspend fun getMielageDetails(@Body claimId: JsonObject): MIleageDetailsResponse

    @POST("edit-split")
    suspend fun updateSplit(@Body claimId: JsonObject): ClaimDetailsResponse


    @POST("upload-claim-attachment")
    suspend fun uploadImage(@Body newClaimRequest: RequestBody): ClaimImageUploadResponse

    @POST()
    suspend fun uploadOcrImage(@Url url:String ,@Body newClaimRequest: RequestBody): OcrUploadResponse

    @Multipart
    @POST("uploadProfileImage")
    suspend fun uploadProfileImage(@Part  claimImage :List<MultipartBody.Part>): ProfilePicUploadResponse

    @Multipart
    @POST("upload-claim-attachment")
    suspend fun uploadClaim(@Part  claimImage :List<MultipartBody.Part>): ProfilePicUploadResponse

    @POST("mileage_list")
    suspend fun mileageList(@Body mileageExpenseRequest: MileageExpenseRequest) : MileageListResponse

    @POST("new_mileageclaim")
    suspend fun createNewMileageClaim(@Body mileageClaimRequest: NewMileageClaimRequest) : CommonResponse

    @POST("edit-mileage-claim")
    suspend fun editNewMileageClaim(@Body mileageClaimRequest: EditMileageClaimRequest) : CommonResponse

    @POST("changePassword")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest) : CommonResponse

    @GET("dropdown")
    suspend fun dropdownData(): DropdownResponse

    @POST("claimed-total")
    suspend fun getClaimedTotal(@Body requestObject: JsonObject): TotailClaimedResponse

    @POST("request_list")
    suspend fun getRequestExpences(@Body mileageExpenseRequest: JsonObject): AcceptRequestResponse

    @POST("request_detail")
    suspend fun getRequestExpencesDetails(@Body mileageExpenseRequest: JsonObject): ExpenceDetailsResponse

    @POST("acc-rej-claim")
    suspend fun acceptReject(@Body data: JsonObject): ExpenceDetailsResponse

    @POST("acc-rej-batch")
    suspend fun acceptRejectBatch(@Body data: JsonObject): ExpenceDetailsResponse

    @GET("logout")
    suspend fun logoutUser(): CommonResponse

    @POST("add-device-token")
    suspend fun addFcmKey(@Body data: JsonObject): CommonResponse

    @GET("my_profile")
    suspend fun profileDetail(): MyProfileResponse

    @POST("edit-profile")
    suspend fun editProfile(@Body data: JsonObject): EditProfileResponse
}
interface GoogleApiService {
    @GET("maps/api/distancematrix/json")
    suspend fun getDistanceInfo(@QueryMap parameters: MutableMap<String, String>): DistanceMatrixResponse

}
