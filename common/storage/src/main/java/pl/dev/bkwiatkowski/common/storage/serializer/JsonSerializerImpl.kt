package pl.dev.bkwiatkowski.common.storage.serializer

import java.lang.reflect.Type
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.serializer
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.storage.JsonSerializer
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either

class JsonSerializerImpl : JsonSerializer {
  private val json = Json {
    serializersModule = SerializersModule {
      contextual(LocalDateTimeSerializer)
      contextual(LocalDateSerializer)
    }
    ignoreUnknownKeys = true
    encodeDefaults = true
    isLenient = true
  }

  override fun <T> serialize(data: T): Either<DomainError, String> = either {
    @Suppress("UNCHECKED_CAST")
    val serializer = json.serializersModule.serializer(data!!::class.java as Class<T>)
    json.encodeToString(serializer, data)
  }

  override fun <T> deserialize(serializedData: String?, type: Type): Either<DomainError, T> = either {
    if (serializedData.isNullOrEmpty()) {
      raise(error = DomainError.Custom(NullPointerException("serializedData is null or empty")))
    }

    @Suppress("UNCHECKED_CAST")
    val serializer = json.serializersModule.serializer(type as Class<T>)
    val result = json.decodeFromString(serializer, serializedData)
    result as T
  }
}
