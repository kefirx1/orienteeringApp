plugins {
  `kotlin-dsl`
}
dependencies {
  implementation(libs.openapi.generator.plugin)
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.kotlin.serialization.gradle.plugin)
}