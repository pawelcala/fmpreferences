import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.FileInputStream
import java.util.*
import java.net.URI

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("maven-publish")
    id("com.jfrog.artifactory") version "4.13.0"
}

kotlin {
    android {
        publishLibraryVariants("release")
    }
    ios {
        binaries {
            framework {
                //baseName = "shared"
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.google.android.material:material:1.2.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13")
            }
        }

        val iosMain by sourceSets
        val iosTest by getting
    }
}



android {

    signingConfigs {
        register("release") {

            val keystorePropertiesFile = file("../keystore.properties")

            if (!keystorePropertiesFile.exists()) {
                logger.warn("Release builds may not work: signing config not found.")
                return@register
            }

            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))

            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    compileSdkVersion(29)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    val framework =
        kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}

tasks.getByName("build").dependsOn(packForXcode)

group = getProjectProperty("BINTRAY_PACKAGE")
version = "0.2"

publishing {
    repositories {
        maven {

            val user = "pawelcala"
            val repo = "maven"
            val name = "fmpreferences"
            url = URI("https://api.bintray.com/maven/$user/$repo/$name/;publish=0;override=1")

            credentials {
                username = getSystemProperty("BINTRAY_USER")
                password = getSystemProperty("BINTRAY_API_KEY")
            }
        }
    }
}
//Get properties defined in Help->Edit Custom Properties
fun getSystemProperty(key: String): String =
    System.getProperty(key) ?: throw IllegalStateException("Key $key not found")

//Get properties defined in in gradle.properties
fun getProjectProperty(key: String): String =
    project.property(key) as? String ?: throw IllegalStateException("Key $key not found")

publishing {
    publications {
        create<MavenPublication>("lib") {
            pom {
                description.set(getProjectProperty("POM_DESCRIPTION"))
                url.set(getProjectProperty("SITE_URL"))
                licenses {
                    license {
                        name.set(getProjectProperty("POM_LICENSE_NAME"))
                        url.set(getProjectProperty("POM_LICENSE_URL"))
//                        distribution = POM_LICENSE_DIST
                    }
                }
                developers {
                    developer {
                        id.set(getProjectProperty("POM_DEVELOPER_ID"))
                        name.set(getProjectProperty("POM_DEVELOPER_NAME"))
                        organization.set(getProjectProperty("POM_ORGANIZATION_NAME"))
                        organizationUrl.set(getProjectProperty("POM_ORGANIZATION_URL"))
                    }
                }
                scm {
                    url.set(getProjectProperty("SITE_URL"))
                }
            }
        }
    }
}