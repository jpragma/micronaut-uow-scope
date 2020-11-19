import org.gradle.api.JavaVersion.VERSION_1_8
plugins {
    id("io.micronaut.library") version "1.0.5"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.1"
    id("pl.allegro.tech.build.axion-release") version "1.10.3"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

group = "com.jpragma"
val artifactId = "micronaut-uow-scope"
version = scmVersion.version

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.jpragma.*")
    }
}

java {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-runtime")
    runtimeOnly("ch.qos.logback:logback-classic")
}

publishing {
    publications {
        create<MavenPublication>("default") {
            groupId = project.group as String
            artifactId = "micronaut-uow-scope"
            version = project.version as String
            from(components["java"])
            pom.withXml {
                asNode().apply {
                    appendNode("name", "MicronautUowScope")
                    appendNode("description", "Micronaut custom scope for Unit-of-work")
                    appendNode("url", "https://github.com/jpragma/micronaut-uow-scope")
                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", "The Apache Software License, Version 2.0")
                        appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")
                        appendNode("distribution", "repo")
                    }
                    appendNode("developers").appendNode("developer").apply {
                        appendNode("name", "Isaac Levin")
                        appendNode("email", "ilevin@jpragma.com")
                        appendNode("organization", "JPragma")
                        appendNode("organizationUrl", "http://blog.jpragma.com/")
                    }
                    appendNode("scm").apply {
                        appendNode("connection", "scm:git:git://github.com/jpragma/micronaut-uow-scope.git")
                        appendNode("developerConnection", "scm:git:ssh://github.com/jpragma/micronaut-uow-scope.git")
                        appendNode("url", "https://github.com/jpragma/micronaut-uow-scope")
                    }
                }
            }
        }
    }
}

fun findProperty(s: String) = project.findProperty(s) as String?

bintray {
    user = findProperty("bintrayUser")
    key = findProperty("bintrayApiKey")
    publish = true
    setPublications("default")
    pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig>{
        repo = "jprmvn"
        name = "micronaut-uow-scope"
        userOrg = "jpragma"
        vcsUrl = "https://github.com/jpragma/micronaut-uow-scope"
        setLabels("java")
        setLicenses("Apache-2.0")
    })
}
