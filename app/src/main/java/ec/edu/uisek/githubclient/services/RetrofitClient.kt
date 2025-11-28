package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.interceptors.BasicAuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.github.com/"
    private var apiService: GithubApiService? = null

    fun createAuthenticatedClient(username: String, token: String): GithubApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(username, token))
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(GithubApiService::class.java)
        return apiService!!
    }

    fun getApiService(): GithubApiService {
        return apiService ?: throw IllegalStateException("El cliente retrofit no ha sido inicializado")
    }
}