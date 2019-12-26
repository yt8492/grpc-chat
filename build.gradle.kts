import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.50"
    id("org.springframework.boot") version "2.2.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("com.google.protobuf") version "0.8.10"
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
}

group = "com.yt8492"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    google()
    jcenter()
}

val grpcVersion = "1.25.0"
val protobufVersion = "3.10.0"
val coroutinesVersion = "1.3.2"
val krotoPlusVersion = "0.5.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    compileOnly("javax.annotation:javax.annotation-api:1.2")

    implementation("io.grpc:grpc-netty:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")

    implementation("com.github.marcoferrer.krotoplus:kroto-plus-coroutines:$krotoPlusVersion")
    implementation("com.github.marcoferrer.krotoplus:kroto-plus-message:$krotoPlusVersion")

    implementation("com.google.protobuf:protobuf-java:$protobufVersion")

    implementation("org.lognet:grpc-spring-boot-starter:2.3.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        // Optional: an artifact spec for a protoc plugin, with "grpc" as
        // the identifier, which can be referred to in the "plugins"
        // container of the "generateProtoTasks" closure.
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("coroutines") {
            artifact = "com.github.marcoferrer.krotoplus:protoc-gen-grpc-coroutines:$krotoPlusVersion:jvm8@jar"
        }
        id("kroto") {
            artifact = "com.github.marcoferrer.krotoplus:protoc-gen-kroto-plus:$krotoPlusVersion:jvm8@jar"
        }
    }
    generateProtoTasks {
        val krotoConfig = file("krotoPlusConfig.asciipb")
        ofSourceSet("main").forEach { task ->
            task.inputs.files(krotoConfig)
            task.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without options.
                id("grpc")
                id("coroutines")
                id("kroto") {
                    outputSubDir = "java"
                    option("ConfigPath=$krotoConfig")
                }
            }
        }
    }
}

sourceSets {
    main {
        proto {
            srcDir("src/main/protobuf")
            include("**/*.protodevel")
        }
        java {
            srcDir("src/main/protobuf")
            srcDir("$buildDir/generated/source/proto/main/java")
            srcDir("$buildDir/generated/source/proto/main/grpc")
            srcDir("$buildDir/generated/source/proto/main/coroutines")
            include("**/*.protodevel")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xuse-experimental=kotlin.Experimental")
        jvmTarget = "1.8"
    }
}
