// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.9.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    // Add this line to define the KSP plugin for the project
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}
