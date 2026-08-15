package pl.dev.bkwiatkowski.common.core.storage.file

import android.net.Uri
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import java.io.File

interface LocalFileManager {
  suspend fun createTempFile(fileName: String, extension: FileExtension): Either<DomainError, File>

  suspend fun readBytesFromUri(uri: Uri): Either<DomainError, ByteArray>
}
