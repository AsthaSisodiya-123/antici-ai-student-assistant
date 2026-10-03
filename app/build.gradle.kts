plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.anticai.studentassistant"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.anticai.studentassistant"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")

    // Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:5.2.1")

    testImplementation(libs.junit)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}