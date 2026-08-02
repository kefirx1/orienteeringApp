package pl.dev.bkwiatkowski.common.camera

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.activityconnector.ActivityForResult
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface CameraActivityConnector : ActivityConnector

interface CameraManager {
  suspend fun takePicture(): Either<DomainError, Uri>
}

class CameraManagerImpl(
  private val context: Context,
) : ActivityForResult<Uri, Boolean>(), CameraManager, CameraActivityConnector {

  override val resultContract: ActivityResultContract<Uri, Boolean> =
    ActivityResultContracts.TakePicture()

  override suspend fun takePicture(): Either<DomainError, Uri> = either {
    val (imageFile, photoUri) = withContext(Dispatchers.IO) {
      val file = createImageFile().getRight()
      val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
      Pair(file, uri)
    }

    val success = launchActivityForResult(intent = photoUri)

    if (success) {
      photoUri
    } else {
      withContext(Dispatchers.IO) {
        imageFile.delete()
      }
      raise(
        error = DomainError.Custom(IllegalStateException("Failed to take picture or user cancelled"))
      )
    }
  }

  private fun createImageFile(): Either<DomainError, File> = either {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
      ?: raise(error = DomainError.Custom(IllegalStateException("External storage directory is not available")))
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    File.createTempFile("IMG_$timeStamp", ".jpg", storageDir)
  }
}