plugins {
  id("org.openapi.generator")
}

openApiGenerate {
  generatorName.set("kotlin")
  inputSpec.set("${rootDir}/technical/backend/swagger/mobile-documentation.yaml")
  outputDir.set("${layout.buildDirectory.get().asFile}/generated/openapi")
  templateDir.set("${rootDir}/build-logic/src/main/resources/openapi-templates")
  packageName.set("pl.dev.bkwiatkowski.technical.backend")
  modelPackage.set("pl.dev.bkwiatkowski.technical.backend.data")
  apiPackage.set("pl.dev.bkwiatkowski.technical.backend.api")
  globalProperties.set(
    mapOf(
      "models" to "",
      "apis" to "",
      "supportingFiles" to "false",
    )
  )
  configOptions.set(
    mapOf(
      "serializationLibrary" to "kotlinx_serialization",
      "dateLibrary" to "java8-localdatetime",
      "enumPropertyNaming" to "UPPERCASE",
    )
  )
  typeMappings.set(
    mapOf(
      "date-time" to "java.time.LocalDateTime",
      "DateTime" to "java.time.LocalDateTime",
    )
  )
  generateModelTests.set(false)
  generateModelDocumentation.set(false)
}

