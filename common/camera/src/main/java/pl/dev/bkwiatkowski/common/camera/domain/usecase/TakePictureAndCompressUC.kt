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

interface TakePictureAndCompressUC : EitherUseCase<UseCase.Params.Empty, ByteArray>

class TakePictureAndCompressUCImpl(
  private val cameraManager: CameraManager,
  private val imageCompressor: ImageCompressor,
  private val localFileManager: LocalFileManager,
) : TakePictureAndCompressUC {

  companion object {
    private const val COMPRESSED_IMAGE_QUALITY_PERCENT = 70
  }

  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, ByteArray> = either {
    val photoUri = cameraManager.takePicture().getRight()

    val originalBytes = localFileManager.readBytesFromUri(photoUri).getRight()

    Log.i(
      tag = Tag(this@TakePictureAndCompressUCImpl),
      message = "Original image size: ${originalBytes.size} bytes",
    )

    imageCompressor.compress(
      bytes = originalBytes,
      qualityPercent = COMPRESSED_IMAGE_QUALITY_PERCENT,
    ).onRight { compressedBytes ->
      Log.i(
        tag = Tag(this@TakePictureAndCompressUCImpl),
        message = "Compressed image size: ${compressedBytes.size} bytes",
      )
    }.getRight()
  }
}