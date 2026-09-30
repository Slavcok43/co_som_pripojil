plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "sk.cosompripojil"
    compileSdk = 35

    defaultConfig {
        applicationId = "sk.cosompripojil"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}