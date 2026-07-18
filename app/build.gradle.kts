import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.dagger)
  alias(libs.plugins.kotlin.ksp)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.google.services)
  alias(libs.plugins.secrets.gradle.plugin)
}

android {
  namespace = "pl.dev.bkwiatkowski.orienteeringapp"
  compileSdk = 37

  defaultConfig {
    applicationId = "pl.dev.bkwiatkowski.orienteeringapp"
    minSdk = 29
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  flavorDimensions += "environment"

  productFlavors {
    create("develop") {
      dimension = "environment"
      buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080\"")
    }
    create("prod") {
      dimension = "environment"
      buildConfigField("String", "API_BASE_URL", "\"https://api.example.com\"")
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_21)
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.appcompat)
  implementation(libs.dagger.hilt)
  ksp(libs.dagger.hilt.compiler)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.ktor.core)
  implementation(libs.ktor.auth)
  implementation(libs.ktor.resources)
  implementation(platform(libs.firebase.bom))

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  implementation(project(":feature:login"))
  implementation(project(":feature:dashboard"))
  implementation(project(":feature:maps"))

  implementation(project(":technical:mobile"))
  implementation(project(":technical:backend"))
  implementation(project(":technical:user"))

  implementation(project(":common:core"))
  implementation(project(":common:ui"))
  implementation(project(":common:loader"))
  implementation(project(":common:activityconnector"))
  implementation(project(":common:storage"))
  implementation(project(":common:network"))
  implementation(project(":common:security"))
  implementation(project(":common:validators"))
  implementation(project(":common:time"))
}
