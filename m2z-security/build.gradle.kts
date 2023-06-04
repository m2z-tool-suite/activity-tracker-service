plugins {
    java
    application
    id("org.springframework.boot") version "3.0.0"
    id("pl.allegro.tech.build.axion-release") version "1.14.0"
    `maven-publish`
}

group = "com.m2z.tools"
version = scmVersion.version
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security:3.0.0")
    testImplementation("org.springframework.security:spring-security-test:3.0.0")
    implementation("com.nimbusds:nimbus-jose-jwt:9.31")
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    annotationProcessor ("org.springframework.boot:spring-boot-configuration-processor:3.0.0")
    implementation("org.springframework.boot:spring-boot-starter-web:3.0.0")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.m2z.tools"
            artifactId = "security"
            version = scmVersion.version

//            from()
        }
//        pom {
//
//        }
        repositories {
        }
    }
}