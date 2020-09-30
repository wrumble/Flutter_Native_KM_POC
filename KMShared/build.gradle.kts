import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlin_version: String by extra

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    targets {
        android()
        val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
                if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
                    ::iosArm64
                else
                    ::iosX64

        iOSTarget("ios") {
            binaries {
                framework {
                    baseName = "KMShared"
                }
            }
        }
    }

    sourceSets["androidMain"].dependencies {
        implementation("com.squareup.sqldelight:android-driver:1.4.3")
    }

    sourceSets["iosMain"].dependencies {
        implementation("com.squareup.sqldelight:native-driver:1.4.3")
    }
}

android {
    compileSdkVersion(28)

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(28)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("androidx.core:core-ktx:+")
}

sqldelight {
    database("KMSharedDatabase") {
        packageName = "com.rumblewayne.bimmultiplatorm"
        sourceFolders = listOf("sqlDelight")
    }
}

val packForXcode by tasks.creating(Sync::class) {
    val targetDir = File(buildDir, "xcode-frameworks")

    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets
            .getByName<KotlinNativeTarget>("ios")
            .binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    from({ framework.outputDirectory })
    into(targetDir)

    /// generate a helpful ./gradlew wrapper with embedded Java path
    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText(
                """
            #!/bin/bash
            export 'JAVA_HOME=${System.getProperty("java.home")}'
            cd '${rootProject.rootDir}'
            ./gradlew $@
            """.trimIndent()
        )
        gradlew.setExecutable(true)
    }
}

tasks.getByName("build").dependsOn(packForXcode)
