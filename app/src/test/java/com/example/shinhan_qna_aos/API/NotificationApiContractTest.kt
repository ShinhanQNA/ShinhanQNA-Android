package com.example.shinhan_qna_aos.API

import com.example.shinhan_qna_aos.FcmTokenRequest
import com.example.shinhan_qna_aos.info.api.OwnStatusRequest
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationApiContractTest {
    @Test
    fun sendsReapplicationAndFcmTokenWithExpectedMethodsAndBodies() = runBlocking {
        val requests = mutableListOf<okhttp3.Request>()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            requests += chain.request()
            val responseBody = if (chain.request().url.encodedPath == "/users/fcm-token") {
                "{\"message\":\"FCM 토큰 저장 완료\"}"
            } else {
                "{}"
            }
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(responseBody.toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        val api = Retrofit.Builder()
            .baseUrl("https://example.test/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIInterface::class.java)

        assertEquals(true, api.updateOwnStatus("Bearer test-access", OwnStatusRequest("가입 대기 중")).isSuccessful)
        assertEquals(true, api.registerFcmToken("Bearer test-access", FcmTokenRequest("test-device-token")).isSuccessful)

        assertEquals("PUT", requests[0].method)
        assertEquals("/users/me/status", requests[0].url.encodedPath)
        assertEquals("{\"status\":\"가입 대기 중\"}", requests[0].body?.let { body ->
            okio.Buffer().also(body::writeTo).readUtf8()
        })
        assertEquals("POST", requests[1].method)
        assertEquals("/users/fcm-token", requests[1].url.encodedPath)
        assertEquals("{\"fcmToken\":\"test-device-token\"}", requests[1].body?.let { body ->
            okio.Buffer().also(body::writeTo).readUtf8()
        })
        assertEquals("Bearer test-access", requests[1].header("Authorization"))
        assertEquals("application/json; charset=UTF-8", requests[1].body?.contentType()?.toString())
    }
}
