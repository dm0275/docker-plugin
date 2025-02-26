package com.fussionlabs.gradle.docker

open class DockerPluginExtension {
    var dockerFilePath = ""
    var registry = ""
    var repository: String = ""
    var platforms = mutableListOf("linux/amd64")
    var buildArgs = mutableMapOf<String, String>()
    var tags = mutableListOf<String>()
    var requireBuild = true
    var applyLatestTag = true

    var username: String = System.getenv("DOCKER_USERNAME") ?: ""
    var password: String = System.getenv("DOCKER_PASSWORD") ?: ""
}