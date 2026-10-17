**gradle/libs.versions.toml añadir:**

[versions]
activityCompose = "1.9.3"
composeBom = "2024.09.00"
kotlin = "2.2.10"

[libraries]
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-foundation = { group = "androidx.compose.foundation", name = "foundation" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }

[plugins]
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }

**build.gradle.kts añadir** 

alias(libs.plugins.kotlin.compose) apply false

**app/build.gradle.kts añadir**

plugins {
   
    alias(libs.plugins.kotlin.compose)
}

android {

    buildFeatures {
        compose = true
    }
}

dependencies {
 
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
}

********AndroidManifest.xml añadir antes de </application> ******

<activity
    android:name=".MainActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

