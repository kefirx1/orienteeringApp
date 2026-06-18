package pl.dev.bkwiatkowski.technical.user.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.security.CryptoManager
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.security.generator.AesKeyGenerator
import pl.dev.bkwiatkowski.common.core.security.generator.SecureRandomGenerator
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.user.data.repository.UserRepository
import pl.dev.bkwiatkowski.technical.user.domain.interactor.UserBackendInteractor
import pl.dev.bkwiatkowski.technical.user.domain.model.UserSettings
import java.time.LocalDateTime

interface CreateNewUserUC : EitherUseCase<CreateNewUserUC.Params, Unit> {
  data class Params(
    val username: String,
    val email: String,
    val password: String,
    val phoneNumber: String?,
    val dateOfBirth: LocalDateTime,
  ): UseCase.Params
}

class CreateNewUserUCImpl(
  private val userBackendInteractor: UserBackendInteractor,
  private val secureRandomGenerator: SecureRandomGenerator,
  private val aesKeyGenerator: AesKeyGenerator,
  private val masterKeyProvider: MasterKeyProvider,
  private val cryptoManager: CryptoManager,
  private val userRepository: UserRepository,
  private val base64Coder: Base64Coder,
) : CreateNewUserUC {
  override suspend fun invoke(params: CreateNewUserUC.Params): Either<DomainError, Unit> = either {
    userBackendInteractor.registerUser(
      username = params.username,
      email = params.email,
      password = params.password,
      phoneNumber = params.phoneNumber,
      dateOfBirth = params.dateOfBirth,
    ).getRight()
    Log.i(
      tag = Tag(this@CreateNewUserUCImpl),
      message = "Remote user created successfully",
    )

    val salt = secureRandomGenerator.getSecret(size = Cryptography.SALT_SIZE)
    val masterKey = aesKeyGenerator.generateMasterKey().getRight()
    Log.i(
      tag = Tag(this@CreateNewUserUCImpl),
      message = "New master key generated successfully",
    )
    masterKeyProvider.saveDecryptedMasterKey(masterKey = masterKey).getRight()
    Log.i(
      tag = Tag(this@CreateNewUserUCImpl),
      message = "Master key saved successfully",
    )

    val kek = aesKeyGenerator.generateSecretKeyFromBase(
      cryptography = Cryptography.AES_GCM_NoPadding,
      base = params.password.toCharArray(),
      salt = salt,
    ).getRight()
    Log.i(
      tag = Tag(this@CreateNewUserUCImpl),
      message = "New KEK generated successfully",
    )

    val ivWithEncryptedMasterKey = cryptoManager.encryptWithKey(
      data = masterKey.encoded,
      key = kek,
      cryptography = Cryptography.AES_GCM_NoPadding,
    ).getRight()
    Log.i(
      tag = Tag(this@CreateNewUserUCImpl),
      message = "Master key encrypted successfully",
    )

    userRepository.saveNewUserSettings(
      userSettings = UserSettings(
        userName = params.username,
        salt = base64Coder.encode(data = salt).getRight(),
        ivDek = base64Coder.encode(data = ivWithEncryptedMasterKey).getRight(),
        ivDekBiometric = null,
      )
    )
  }
}