plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.simulascore"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.simulascore"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.android.volley:volley:1.2.1")

    implementation("com.github.bumptech.glide:glide:4.12.0")

    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.12.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")







}
