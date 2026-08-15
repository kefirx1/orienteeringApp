package pl.dev.bkwiatkowski.common.camera.image

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.image.ImageCompressor
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either

class ImageCompressorImpl : ImageCompressor {
  override suspend fun compress(bytes: ByteArray, qualityPercent: Int): Either<DomainError, ByteArray> = either {
    withContext(Dispatchers.IO) {
      val options = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
      val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        ?: raise(error = DomainError.Custom(IllegalArgumentException("Failed to decode image bytes")))

      val out = ByteArrayOutputStream()
      val quality = when {
        qualityPercent < 0 -> 0
        qualityPercent > 100 -> 100
        else -> qualityPercent
      }

      val compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
      if (!compressed) {
        raise(error = DomainError.Custom(IllegalStateException("Failed to compress bitmap to JPEG")))
      }

      out.toByteArray()
    }
  }
}
