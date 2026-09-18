package com.example.shinhan_qna_aos

import android.util.Log

internal fun debugLog(tag: String, message: String) {
    if (BuildConfig.DEBUG) Log.d(tag, message)
}
