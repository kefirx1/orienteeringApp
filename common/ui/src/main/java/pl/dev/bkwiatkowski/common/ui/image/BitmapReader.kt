package pl.dev.bkwiatkowski.common.ui.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64


fun interface BitmapReader {
  fun decode(encoded: String): Bitmap?
}

class BitmapReaderImpl : BitmapReader {
  override fun decode(encoded: String): Bitmap? {
    if (encoded.isEmpty()) return null

    return try {
      val bytes = Base64.decode(encoded, Base64.NO_WRAP)
      BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
      null
    }
  }
}
