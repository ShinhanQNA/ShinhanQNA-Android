package com.example.shinhan_qna_aos.API

import com.example.shinhan_qna_aos.BuildConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIRetrofit {
    private const val BASE_URL =  BuildConfig.BASE_URL

    // 서버의 JSON 필드명이 snake_case인 경우, Gson이 camelCase로 자동 변환하도록 설정
    // 예: access_token -> accessToken
    private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setLenient()
        .create()

    // HTTP 통신 로그를 보기 위한 인터셉터
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // BODY 레벨로 설정하면 요청/응답 라인, 헤더, 바디를 모두 볼 수 있습니다.
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        // 로깅 인터셉터를 추가하여 모든 API 통신을 로그로 확인
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()

    val apiService: APIInterface by lazy {
        retrofit.create(APIInterface::class.java)
    }
}