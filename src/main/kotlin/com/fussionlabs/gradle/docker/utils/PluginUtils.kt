package com.fussionlabs.gradle.docker.utils

import com.fussionlabs.gradle.docker.DockerPluginExtension
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import java.io.File
import java.util.concurrent.TimeUnit
import com.fussionlabs.gradle.docker.DOCKER_DEFAULT_REGISTRY

object PluginUtils {
    val logger = Logging.getLogger(PluginUtils::class.java)
    val Project.dockerExt: DockerPluginExtension
        get() = extensions.getByType(DockerPluginExtension::class.java)

    fun exec(command: List<String>, workingDir: File? = null, timeout:Long = 60): Pair<Int, String> {
        var output = ""
        val processBuilder = ProcessBuilder(command)
        logger.info("Running command: $command")
        if (workingDir != null) {
            processBuilder.directory(workingDir)
        }

        val process = processBuilder.start()
        output += process.inputStream.bufferedReader().readText()
        output += process.errorStream.bufferedReader().readText()

        process.waitFor(timeout, TimeUnit.SECONDS)
        logger.info("Exit Code: ${process.exitValue()}")
        logger.info("Output: $output")

        return Pair(process.exitValue(), output)
    }

    fun getImageTags(dockerExt: DockerPluginExtension): List<String> {
        val imageTags = mutableListOf<String>()
        val repository = if (dockerExt.registry.isNotEmpty() && dockerExt.registry != DOCKER_DEFAULT_REGISTRY) {
            "${dockerExt.registry}/${dockerExt.repository}"
        } else {
            dockerExt.repository
        }
        logger.debug("Using full registry/repository '{}'", repository)

        dockerExt.tags.forEach { tag ->
            imageTags.add("$repository:$tag")
        }

        if (dockerExt.applyLatestTag) {
            imageTags.add("$repository:latest")
        }

        return imageTags
    }
}