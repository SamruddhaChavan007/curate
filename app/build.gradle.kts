import com.android.build.api.dsl.ApplicationExtension
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { input ->
            load(input)
        }
    }
}

fun localProperty(name: String): String = localProperties.getProperty(name).orEmpty()

fun String.asBuildConfigString(): String {
    return "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""
}

extensions.configure<ApplicationExtension> {
    namespace = "com.example.curate"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.curate"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "LOGS_ENABLED", "true")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }

        release {
            buildConfigField("boolean", "LOGS_ENABLED", "false")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", localProperty("DEV_BASE_URL").asBuildConfigString())
            buildConfigField("String", "SUPABASE_URL", localProperty("DEV_BASE_URL").asBuildConfigString())
            buildConfigField("String", "SUPABASE_ANON_KEY", localProperty("SUPABASE_ANON_KEY").asBuildConfigString())
            buildConfigField("String", "UNSPLASH_ACCESS_KEY", localProperty("UNSPLASH_ACCESS_KEY").asBuildConfigString())
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }

        create("staging") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", localProperty("STAGING_BASE_URL").asBuildConfigString())
            buildConfigField("String", "SUPABASE_URL", localProperty("STAGING_BASE_URL").asBuildConfigString())
            buildConfigField("String", "SUPABASE_ANON_KEY", localProperty("SUPABASE_ANON_KEY").asBuildConfigString())
            buildConfigField("String", "UNSPLASH_ACCESS_KEY", localProperty("UNSPLASH_ACCESS_KEY").asBuildConfigString())
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
        }

        create("production") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", localProperty("PRODUCTION_BASE_URL").asBuildConfigString())
            buildConfigField("String", "SUPABASE_URL", localProperty("PRODUCTION_BASE_URL").asBuildConfigString())
            buildConfigField("String", "SUPABASE_ANON_KEY", localProperty("SUPABASE_ANON_KEY").asBuildConfigString())
            buildConfigField("String", "UNSPLASH_ACCESS_KEY", localProperty("UNSPLASH_ACCESS_KEY").asBuildConfigString())
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.material)
    implementation(libs.timber)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(platform(libs.supabase.bom))
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation("androidx.core:core-splashscreen:1.0.0")
}
