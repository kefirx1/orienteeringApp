package pl.dev.bkwiatkowski.common.camera.domain.usecase

import pl.dev.bkwiatkowski.common.camera.CameraManager
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.image.ImageCompressor
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.storage.file.LocalFileManager
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either

interface TakePictureAndCompressUC : EitherUseCase<TakePictureAndCompressUC.Params, ByteArray> {
  data class Params(
    val compressedQualityPercent: Int,
    val maxCompressedImageSizeBytes: Int,
  ) : UseCase.Params
}

class TakePictureAndCompressUCImpl(
  private val cameraManager: CameraManager,
  private val imageCompressor: ImageCompressor,
  private val localFileManager: LocalFileManager,
) : TakePictureAndCompressUC {

  private val photoError = DomainError.Business(
    message = "Nie udało się zrobić zdjęcia",
    primaryButtonLabel = "Zamknij",
  )

  override suspend fun invoke(params: TakePictureAndCompressUC.Params): Either<DomainError, ByteArray> = either {
    val photoUri = cameraManager.takePicture().mapLeft { photoError }.getRight()
    val originalBytes = localFileManager.readBytesFromUri(photoUri).mapLeft { photoError }.getRight()

    Log.i(
      tag = Tag(this@TakePictureAndCompressUCImpl),
      message = "Original image size: ${originalBytes.size} bytes",
    )

    val compressedBytes = imageCompressor.compress(
      bytes = originalBytes,
      qualityPercent = params.compressedQualityPercent,
    ).onRight { compressedBytes ->
      Log.i(
        tag = Tag(this@TakePictureAndCompressUCImpl),
        message = "Compressed image size: ${compressedBytes.size} bytes",
      )
    }.mapLeft { photoError }.getRight()

    if (compressedBytes.isEmpty()) {
      raise(error = photoError)
    }

    if (compressedBytes.size > params.maxCompressedImageSizeBytes) {
      raise(
        error = DomainError.Business(
          message = "Rozmiar zdjęcia przekracza maksymalny dozwolony rozmiar ${params.maxCompressedImageSizeBytes / 1024} KB",
          primaryButtonLabel = "Zamknij",
        ),
      )
    }

    compressedBytes
  }
}