import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
}

val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

val keysProperties = Properties().apply {
    val f = rootProject.file("keys.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

/** Prefer [keys.properties], fall back to [local.properties] for Twitch credentials. */
fun twitchCredential(key: String): String =
    keysProperties.getProperty(key)?.takeIf { it.isNotBlank() }
        ?: localProperties.getProperty(key, "")

extensions.configure<ApplicationExtension>("android") {
    compileSdk = 36
    namespace = "com.huhx0015.gamecollection"

    defaultConfig {
        applicationId = "com.huhx0015.gamecollection"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "TWITCH_CLIENT_ID",
            "\"${twitchCredential("twitch.client.id")}\"",
        )
        buildConfigField(
            "String",
            "TWITCH_CLIENT_SECRET",
            "\"${twitchCredential("twitch.client.secret")}\"",
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:architecture"))
    implementation(project(":core:common"))

    implementation(libs.androidx.annotation)
    implementation(libs.androidx.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.common.j8)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.animation.graphics)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.coil)
    implementation(libs.coil.svg)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)

    implementation(libs.timber)

    debugImplementation(libs.leakcanary.android)

    androidTestImplementation(libs.androidx.test.espresso.core) {
        exclude(group = "com.google.code.findbugs", module = "jsr305")
    }
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.kotlin)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
