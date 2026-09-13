package com.example.shinhan_qna_aos

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.shinhan_qna_aos.API.APIRetrofit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiInterface = APIRetrofit.apiService
        // 4. Compose 시작
        setContent {
            AppNavigation(
                apiInterface = apiInterface
            )
        }
    }
}