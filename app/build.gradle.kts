import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.shinhan_qna_aos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.shinhan_qna_aos"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // 카카오
        buildConfigField("String", "KAKAO_APP_KEY", "\"${localProperties["KAKAO_APP_KEY"]}\"")
        buildConfigField("String", "KAKAO_REST_API", "\"${localProperties["KAKAO_REST_API"]}\"")
        manifestPlaceholders["KAKAO_APP_KEY"] = "${localProperties["KAKAO_APP_KEY"]}"
        //구글
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${localProperties["GOOGLE_WEB_CLIENT_ID"]}\"")
        buildConfigField("String", "GOOGLE_APP_CLIENT_ID", "\"${localProperties["GOOGLE_APP_CLIENT_ID"]}\"")

        // 우리서버
        buildConfigField("String","BASE_URL","\"${localProperties["BASE_URL"]}\"")

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.navigation.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // 온보딩 페이지 카운트 의존성
    implementation (libs.accompanist.pager)
    // 데이터 바인딩 의존성
    implementation (libs.androidx.datastore.preferences)
    // 아이콘 추가
    implementation(libs.lucide.icons)
    // Google 로그인용
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-auth:21.4.0")
    // Kakao SDK (implementation 버전 확인 필수)
    implementation("com.kakao.sdk:v2-user:2.21.5")  // 사용자 정보, 로그인
    implementation("com.kakao.sdk:v2-auth:2.21.5")  // 로그인
    // Kotlin Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    //API
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    //uri
    implementation("io.coil-kt:coil-compose:2.7.0")
    //바텀시트 의존성
    implementation("androidx.compose.material:material:1.9.0")
}