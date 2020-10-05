# BIMMultiPlatform

A POC to see how to connect Flutter to Native Android and iOS though Platform Channels and Platform Views while connecting to Kotlin Multiplatform for business logic and databasing.

## Getting Started

clone the repo

`cd BIMMultiPlatform/android`

`./gradlew build`

`./gradlew :KMShared:packForXCode`

Open the BIMMultiPlatform root folder in Android Studio and once gradle has finished syncing, if you do not have it already install the flutter plugin, then select to open an android or iOS simulator and run.
