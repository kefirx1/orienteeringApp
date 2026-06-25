package pl.dev.bkwiatkowski.technical.user.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.security.CryptoManager
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.security.provider.AppSecretKeyProvider
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.user.domain.repository.UserRepository
import javax.crypto.spec.SecretKeySpec

interface LoginUserLocalUC : EitherUseCase<UseCase.Params.Empty, Unit>

class LoginUserLocalUCImpl(
  private val userRepository: UserRepository,
  private val cryptoManager: CryptoManager,
  private val masterKeyProvider: MasterKeyProvider,
  private val appSecretKeyProvider: AppSecretKeyProvider,
  private val base64Coder: Base64Coder,
): LoginUserLocalUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, Unit> =
    either {
      Log.i(
        tag = Tag(this@LoginUserLocalUCImpl),
        message = "Starting login user local use case",
      )
      val userSettings = userRepository.getUserSettings().getRight()
      Log.i(
        tag = Tag(this@LoginUserLocalUCImpl),
        message = "Got user settings successfully",
      )

      val kek = appSecretKeyProvider.getKeyStoreSecretKey(
        cryptography = Cryptography.AES_GCM_NoPadding,
      ).getRight()
      Log.i(
        tag = Tag(this@LoginUserLocalUCImpl),
        message = "Retrieved Key Encryption Key (KEK) successfully",
      )

      val dek = cryptoManager.decryptWithKey(
        data = base64Coder.decode(data = userSettings.ivDek).getRight(),
        key = kek,
        cryptography = Cryptography.AES_GCM_NoPadding,
      ).getRight()
      Log.i(
        tag = Tag(this@LoginUserLocalUCImpl),
        message = "Decrypted DEK successfully",
      )

      masterKeyProvider.saveDecryptedMasterKey(
        masterKey = SecretKeySpec(dek, Cryptography.AES_GCM_NoPadding.algorithm),
      ).getRight()
    }
}