package com.example.shinhan_qna_aos.login.api

import android.app.Application
import com.example.shinhan_qna_aos.BuildConfig
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }
}