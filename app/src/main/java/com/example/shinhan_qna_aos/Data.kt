package com.example.shinhan_qna_aos

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class Data(private val context: Context) {
    private val prefs = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    private val tokenCipher = TokenCipher()

    companion object Companion {
        private const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val KEY_REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val KEY_ACCESS_TOKEN_EXP = "ACCESS_TOKEN_EXP"
        private const val KEY_REFRESH_TOKEN_EXP = "REFRESH_TOKEN_EXP"
        private const val KEY_ONBOARDED = "ONBOARDED"
        private const val KEY_IS_ADMIN = "IS_ADMIN"
        private const val KEY_USER_STATUS = "USER_STATUS" // 유저 가입 상태
        private const val KEY_USER_NAME = "USER_NAME" // 유저 이름
        private const val KEY_USER_INFO_SUBMITTED = "USER_INFO_SUBMITTED" // 가입 요청 여부
        private const val KEY_USER_EMAIL = "USER_EMAIL"
        private const val KEY_APPEAL_COMPLETED = "APPEAL_COMPLETED" // 이의신청 완료 여부 추가
        private const val ENCRYPTED_TOKEN_PREFIX = "v1:"
    }

    var accessToken: String?  // 엑세스 토큰
        get() = getToken(KEY_ACCESS_TOKEN)
        set(value) = putToken(KEY_ACCESS_TOKEN, value)

    var refreshToken: String? // 리프래쉬 토큰
        get() = getToken(KEY_REFRESH_TOKEN)
        set(value) = putToken(KEY_REFRESH_TOKEN, value)

    var accessTokenExpiresAt: Long // 엑세스 만료
        get() = prefs.getLong(KEY_ACCESS_TOKEN_EXP, 0L)
        set(value) = prefs.edit().putLong(KEY_ACCESS_TOKEN_EXP, value).apply()

    var refreshTokenExpiresAt: Long // 리프래쉬 만료
        get() = prefs.getLong(KEY_REFRESH_TOKEN_EXP, 0L)
        set(value) = prefs.edit().putLong(KEY_REFRESH_TOKEN_EXP, value).apply()

    var onboarding: Boolean // 관리자 관련
        get() = prefs.getBoolean(KEY_ONBOARDED, true)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDED, value).apply()

    var isAdmin: Boolean // 관리자 관련
        get() = prefs.getBoolean(KEY_IS_ADMIN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_ADMIN, value).apply()

    var studentCertified: Boolean  // 정보 요청 처음 하는 건지 파악
        get() = prefs.getBoolean(KEY_USER_INFO_SUBMITTED, false)
        set(value) = prefs.edit().putBoolean(KEY_USER_INFO_SUBMITTED, value).apply()

    var userStatus: String? // 유저 상태 저장
        get() = prefs.getString(KEY_USER_STATUS, null)
        set(value) = prefs.edit().putString(KEY_USER_STATUS, value).apply()

    var userName: String? // 유저 이름
        get() = prefs.getString(KEY_USER_NAME, null)
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var userEmail: String? // 유저 이메일
        get() = prefs.getString(KEY_USER_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    var isAppealCompleted: Boolean
        get() = prefs.getBoolean(KEY_APPEAL_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_APPEAL_COMPLETED, value).apply()

    // 토큰 저장
    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresInSeconds: Int,
        refreshTokenExpiresInSeconds: Long? = null
    ) {
        val now = System.currentTimeMillis()
        this.accessToken = accessToken.trim()
        this.refreshToken = refreshToken.trim()
        this.accessTokenExpiresAt = now + expiresInSeconds * 1000L
        this.refreshTokenExpiresAt = refreshTokenExpiresInSeconds?.let {
            now + it * 1000L
        } ?: (now + 7L * 24 * 60 * 60 * 1000) // 기본 7일
    }

    // 토큰 만료 여부 체크
    fun isAccessTokenExpired(): Boolean = System.currentTimeMillis() >= accessTokenExpiresAt
    fun isRefreshTokenExpired(): Boolean = System.currentTimeMillis() >= refreshTokenExpiresAt

    //로그아웃 관련
    fun clearTokens() {
        accessToken = null
        refreshToken = null
        accessTokenExpiresAt = 0
        refreshTokenExpiresAt = 0
        isAdmin = false
    }

    // 재차단 등 상태 변경 시 호출해 이의신청 완료 상태를 초기화 가능
    fun clearAppealCompleted() {
        isAppealCompleted = false
    }

    private fun getToken(key: String): String? {
        val storedValue = prefs.getString(key, null) ?: return null
        if (!storedValue.startsWith(ENCRYPTED_TOKEN_PREFIX)) {
            putToken(key, storedValue)
            return storedValue
        }
        return tokenCipher.decrypt(storedValue)
    }

    private fun putToken(key: String, value: String?) {
        prefs.edit().putString(key, value?.let(tokenCipher::encrypt)).apply()
    }

}

private class TokenCipher {
    private val keyStore = KeyStore.getInstance(KEY_STORE).apply { load(null) }

    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val encryptedValue = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val payload = byteArrayOf(cipher.iv.size.toByte()) + cipher.iv + encryptedValue
        return ENCRYPTED_TOKEN_PREFIX + Base64.encodeToString(payload, Base64.NO_WRAP)
    }

    fun decrypt(value: String): String? = runCatching {
        val payload = Base64.decode(value.removePrefix(ENCRYPTED_TOKEN_PREFIX), Base64.NO_WRAP)
        val ivSize = payload.first().toInt() and 0xFF
        require(payload.size > ivSize + 1)
        val iv = payload.copyOfRange(1, ivSize + 1)
        val encryptedValue = payload.copyOfRange(ivSize + 1, payload.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(TAG_LENGTH_BITS, iv))
        cipher.doFinal(encryptedValue).toString(Charsets.UTF_8)
    }.getOrNull()

    private fun secretKey(): SecretKey =
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
            ?: KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE).run {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                )
                generateKey()
            }

    private companion object {
        const val KEY_STORE = "AndroidKeyStore"
        const val KEY_ALIAS = "shinhan_qna_token_key"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val ENCRYPTED_TOKEN_PREFIX = "v1:"
        const val TAG_LENGTH_BITS = 128
    }
}
