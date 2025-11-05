android {
    // ...
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // ...
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // RecyclerView (untuk daftar riwayat)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Coroutines (untuk timer dan proses async)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Google AdMob
    implementation("com.google.android.gms:play-services-ads:23.1.0")

    // Retrofit (untuk koneksi ke backend ANDA)
    // implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}