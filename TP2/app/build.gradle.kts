plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) version "2.1.0"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

secrets {
    propertiesFileName = "secrets.properties"

    defaultPropertiesFileName = "local.defaults.properties"
}



android {
    namespace = "com.example.polybluetoothmap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.polybluetoothmap"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {


    implementation(libs.gson)
    implementation(libs.play.services.maps.v1900)
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.ktx.v190)
    //implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)
    implementation(libs.firebase.crashlytics.buildtools)
    ksp(libs.androidx.room.compiler)
//    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}