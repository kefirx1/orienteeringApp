package pl.dev.bkwiatkowski.common.storage.local

import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.storage.file.FileExtension
import pl.dev.bkwiatkowski.common.core.storage.file.LocalFileManager
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import java.io.File

class LocalFileManagerImpl(
  private val context: Context,
) : LocalFileManager {

  override suspend fun createTempFile(fileName: String, extension: FileExtension): Either<DomainError, File> = either {
    withContext(Dispatchers.IO) {
      val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ?: raise(error = DomainError.Custom(IllegalStateException("External storage directory is not available")))

      val prefix = when {
        fileName.length >= 3 -> fileName
        else -> fileName.padEnd(3, 'x')
      }

      File.createTempFile(prefix, extension.value, storageDir)
    }
  }

  override suspend fun readBytesFromUri(uri: Uri): Either<DomainError, ByteArray> = either {
    withContext(Dispatchers.IO) {
      context.contentResolver.openInputStream(uri)?.use { stream ->
        stream.readBytes()
      } ?: raise(error = DomainError.Custom(IllegalStateException("Failed to open input stream for uri: $uri")))
    }
  }
}