package com.example.shinhan_qna_aos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * 이미지 URI를 지정한 최대 크기 이하로 JPEG 압축해 임시 파일로 저장하는 유틸리티 클래스
 */
class ImageUtils {
    companion object {
        suspend fun compressImage(
            context: Context,
            imageUri: Uri,
        ): File? = withContext(Dispatchers.IO) {
            try {
                // 이미지 Uri로부터 InputStream 얻기 (권한 필요 가능성 있음)
                val inputStream = context.contentResolver.openInputStream(imageUri)
                    ?: return@withContext null
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                if (originalBitmap == null) return@withContext null

                // 임시 파일 생성 경로(캐시)
                val compressedFile = File(
                    context.cacheDir,
                    "compressed_image_${System.currentTimeMillis()}.jpg"
                )
                var quality = 100 // JPEG 품질 초기값
                var fileSizeKB: Long

                // 반복적으로 파일 골격 압축하여 원하는 사이즈 이하로 만듦
                do {
                    if (compressedFile.exists()) compressedFile.delete()
                    FileOutputStream(compressedFile).use { outputStream ->
                        val compressed = originalBitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            quality,
                            outputStream
                        )
                        if (!compressed) return@withContext null // 압축 실패시 null
                        outputStream.flush()
                    }
                    fileSizeKB = compressedFile.length() / 1024
                    quality -= 5 // 품질 단계적으로 내리면서 파일 크기 줄임
                } while (fileSizeKB > 8 * 1024 && quality > 5)

                // 조건 만족하면 파일 반환
                compressedFile
            } catch (e: Exception) {
                // 예외 발생 시 null 반환
                null
            }
        }
    }
}
