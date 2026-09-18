package com.example.shinhan_qna_aos.API

import com.example.shinhan_qna_aos.BuildConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIRetrofit {
    private val baseUrl = requireHttpsBaseUrl(BuildConfig.BASE_URL)

    // 서버의 JSON 필드명이 snake_case인 경우, Gson이 camelCase로 자동 변환하도록 설정
    // 예: access_token -> accessToken
    private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setLenient()
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()

    val apiService: APIInterface by lazy {
        retrofit.create(APIInterface::class.java)
    }
}

internal fun requireHttpsBaseUrl(baseUrl: String): String {
    require(baseUrl.startsWith("https://")) { "BASE_URL must use HTTPS" }
    return baseUrl
}
