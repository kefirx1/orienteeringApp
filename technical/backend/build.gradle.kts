plugins {
  id("openapi-generate")
  alias(libs.plugins.android.library)
  alias(libs.plugins.dagger)
  alias(libs.plugins.kotlin.ksp)
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "pl.dev.bkwiatkowski.technical.backend"
  compileSdk {
    version = release(37)
  }

  defaultConfig {
    minSdk = 29

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  sourceSets {
    getByName("main") {
      kotlin.srcDirs("build/generated/openapi/src/main/kotlin")
    }
  }

}

val openApiGenerateTask = tasks.named("openApiGenerate")

tasks.matching { task ->
  (task.name.startsWith("compile") || task.name.startsWith("ksp")) && task.name.endsWith("Kotlin")
}.configureEach {
  dependsOn(openApiGenerateTask)
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.dagger.hilt)
  ksp(libs.dagger.hilt.compiler)
  implementation(libs.kotlinx.serialization)
  implementation(libs.ktor.resources)

  testImplementation(libs.junit)

  implementation(project(":common:core"))
  implementation(project(":common:network"))
}