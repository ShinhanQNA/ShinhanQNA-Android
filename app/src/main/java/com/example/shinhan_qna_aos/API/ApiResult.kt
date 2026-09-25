package com.example.shinhan_qna_aos.API

import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException

internal enum class ApiFailureKind {
    AUTHENTICATION,
    NETWORK,
    HTTP,
    EMPTY_RESPONSE
}

internal class ApiFailureException(
    val kind: ApiFailureKind,
    message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null
) : Exception(message, cause)

internal fun bearerHeader(
    token: String?,
    missingMessage: String = "로그인 토큰이 없습니다."
): String {
    val value = token?.trim().orEmpty()
    if (value.isEmpty()) throw ApiFailureException(ApiFailureKind.AUTHENTICATION, missingMessage)
    return "Bearer $value"
}

internal suspend fun <T> apiResult(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (error: CancellationException) {
    throw error
} catch (error: ApiFailureException) {
    Result.failure(error)
} catch (error: IOException) {
    Result.failure(
        ApiFailureException(
            kind = ApiFailureKind.NETWORK,
            message = "네트워크 연결을 확인해 주세요.",
            cause = error
        )
    )
} catch (error: Exception) {
    Result.failure(error)
}

internal fun Response<*>.successOrThrow(
    httpMessage: String = "서버 오류가 발생했습니다."
) {
    if (!isSuccessful) {
        throw ApiFailureException(
            kind = if (code() in setOf(401, 403)) {
                ApiFailureKind.AUTHENTICATION
            } else {
                ApiFailureKind.HTTP
            },
            message = httpMessage,
            statusCode = code()
        )
    }
}

internal fun <T> Response<T>.bodyOrThrow(
    emptyMessage: String = "응답 데이터가 없습니다.",
    httpMessage: String = "서버 오류가 발생했습니다."
): T {
    successOrThrow(httpMessage)
    return body() ?: throw ApiFailureException(ApiFailureKind.EMPTY_RESPONSE, emptyMessage)
}
