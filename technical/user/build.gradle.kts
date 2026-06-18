plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.dagger)
  alias(libs.plugins.kotlin.ksp)
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "pl.dev.bkwiatkowski.technical.user"
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

}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.dagger.hilt)
  ksp(libs.dagger.hilt.compiler)
  implementation(libs.androidx.datastore)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)
  implementation(libs.kotlinx.serialization)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)

  implementation(project(":common:core"))
}