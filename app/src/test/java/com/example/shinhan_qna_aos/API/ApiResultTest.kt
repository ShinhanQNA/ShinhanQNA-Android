package com.example.shinhan_qna_aos.API

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ApiResultTest {
    @Test
    fun createsBearerHeaderFromToken() {
        assertEquals("Bearer token", bearerHeader(" token "))
    }

    @Test
    fun rejectsMissingTokenAsAuthenticationFailure() {
        val error = assertThrows(ApiFailureException::class.java) { bearerHeader(" ") }

        assertEquals(ApiFailureKind.AUTHENTICATION, error.kind)
    }

    @Test
    fun distinguishesHttpAndEmptyResponseFailures() {
        val authenticationError = assertThrows(ApiFailureException::class.java) {
            Response.error<String>(401, "error".toResponseBody()).bodyOrThrow()
        }
        val httpError = assertThrows(ApiFailureException::class.java) {
            Response.error<String>(500, "error".toResponseBody()).bodyOrThrow()
        }
        val emptyError = assertThrows(ApiFailureException::class.java) {
            Response.success<String>(null).bodyOrThrow()
        }

        assertEquals(ApiFailureKind.AUTHENTICATION, authenticationError.kind)
        assertEquals(ApiFailureKind.HTTP, httpError.kind)
        assertEquals(500, httpError.statusCode)
        assertEquals(ApiFailureKind.EMPTY_RESPONSE, emptyError.kind)
    }

    @Test
    fun mapsIoFailureAndPropagatesCancellation() {
        val networkResult = runBlocking { apiResult<Unit> { throw IOException() } }

        assertEquals(ApiFailureKind.NETWORK, (networkResult.exceptionOrNull() as ApiFailureException).kind)
        assertThrows(CancellationException::class.java) {
            runBlocking { apiResult<Unit> { throw CancellationException() } }
        }
    }
}
