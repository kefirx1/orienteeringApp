package pl.dev.bkwiatkowski.common.storage.coder

import android.util.Base64
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either

class Base64CoderImpl : Base64Coder {
  override fun decode(data: String?): Either<DomainError, ByteArray> = either {
    if (data.isNullOrEmpty()) {
      ByteArray(0)
    } else {
      Base64.decode(data, Base64.NO_WRAP)
    }
  }

  override fun encode(data: ByteArray?): Either<DomainError, String> = either {
    if (data == null) {
      ""
    } else {
      Base64.encodeToString(data, Base64.NO_WRAP)
    }
  }
}