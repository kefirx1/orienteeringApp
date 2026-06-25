package pl.dev.bkwiatkowski.technical.user.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.security.CryptoManager
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.security.generator.AesKeyGenerator
import pl.dev.bkwiatkowski.common.core.security.generator.SecureRandomGenerator
import pl.dev.bkwiatkowski.common.core.security.provider.AppSecretKeyProvider
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.user.domain.model.UserSettings
import pl.dev.bkwiatkowski.technical.user.domain.repository.UserRepository

interface CreateNewLocalUserUC : EitherUseCase<CreateNewLocalUserUC.Params, Unit> {
  data class Params(
    val username: String,
  ): UseCase.Params
}

class CreateNewLocalUserUCImpl(
  private val secureRandomGenerator: SecureRandomGenerator,
  private val aesKeyGenerator: AesKeyGenerator,
  private val appSecretKeyProvider: AppSecretKeyProvider,
  private val masterKeyProvider: MasterKeyProvider,
  private val cryptoManager: CryptoManager,
  private val userRepository: UserRepository,
  private val base64Coder: Base64Coder,
) : CreateNewLocalUserUC {
  override suspend fun invoke(params: CreateNewLocalUserUC.Params): Either<DomainError, Unit> = either {
    val salt = secureRandomGenerator.getSecret(size = Cryptography.SALT_SIZE)
    val masterKey = aesKeyGenerator.generateMasterKey().getRight()
    Log.i(
      tag = Tag(this@CreateNewLocalUserUCImpl),
      message = "New master key generated successfully",
    )
    masterKeyProvider.saveDecryptedMasterKey(masterKey = masterKey).getRight()
    Log.i(
      tag = Tag(this@CreateNewLocalUserUCImpl),
      message = "Master key saved successfully",
    )

    val kek = appSecretKeyProvider.getKeyStoreSecretKey(
      cryptography = Cryptography.AES_GCM_NoPadding,
    ).getRight()
    Log.i(
      tag = Tag(this@CreateNewLocalUserUCImpl),
      message = "Key encryption key (KEK) retrieved successfully",
    )

    val ivWithEncryptedMasterKey = cryptoManager.encryptWithKey(
      data = masterKey.encoded,
      key = kek,
      cryptography = Cryptography.AES_GCM_NoPadding,
    ).getRight()
    Log.i(
      tag = Tag(this@CreateNewLocalUserUCImpl),
      message = "Master key encrypted successfully",
    )

    userRepository.saveNewUserSettings(
      userSettings = UserSettings(
        userName = params.username,
        salt = base64Coder.encode(data = salt).getRight(),
        ivDek = base64Coder.encode(data = ivWithEncryptedMasterKey).getRight(),
      )
    ).getRight()
  }
}