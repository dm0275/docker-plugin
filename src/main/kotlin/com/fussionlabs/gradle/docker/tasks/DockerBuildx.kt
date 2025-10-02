package com.fussionlabs.gradle.docker.tasks

import com.fussionlabs.gradle.docker.DOCKER_PLUGIN_BUILDER
import com.fussionlabs.gradle.docker.utils.PluginUtils
import com.fussionlabs.gradle.docker.utils.PluginUtils.dockerExt
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input

open class DockerBuildx: DockerTask() {
    @Input
    var pushImage: Boolean = false

    @Input
    var loadImage: Boolean = System.getenv("DOCKER_PLUGIN_LOAD_IMAGE").toBoolean()

    override fun exec() {
        val platforms = project.dockerExt.platforms
        val buildArgs = project.dockerExt.buildArgs
        val tags = PluginUtils.getImageTags(project.dockerExt)
        val buildCmd = mutableListOf("buildx", "build")

        // Configure the builder
        setupBuilder()

        // Set the builder
        buildCmd.addAll(listOf("--builder", DOCKER_PLUGIN_BUILDER))

        // Configure the platforms
        buildCmd.addAll(listOf("--platform", platforms.joinToString(",")))

        // Configure the build arguments
        buildArgs.forEach { (key, value) ->
            buildCmd.addAll(listOf("--build-arg", "$key=$value"))
        }

        // Configure the tags
        tags.forEach { tag ->
            buildCmd.addAll(listOf("--tag", tag))
        }

        // Set the image build path
        buildCmd.add(project.dockerExt.dockerFilePath)

        // Load the images (if true)
        if (loadImage) {
            buildCmd.add("--load")
        }

        // Push the images (if true)
        if (pushImage) {
            buildCmd.add("--push")
        }

        logger.info("Build Cmd: $buildCmd")
        dockerTaskArgs = buildCmd
        super.exec()

        logger.lifecycle("Done")
    }

    fun setupBuilder() {
        val (listExitCode, listOutput) = PluginUtils.exec(listOf("docker", "buildx", "ls"))
        if (listExitCode != 0) {
            throw GradleException("Unable to list docker builders. ERROR:  $listOutput")
        }

        if (!listOutput.contains(DOCKER_PLUGIN_BUILDER)) {
            val (createExitCode, createOutput) = PluginUtils.exec(listOf("docker", "buildx", "create", "--name", DOCKER_PLUGIN_BUILDER, "--driver", "docker-container"))
            if (createExitCode != 0) {
                throw GradleException("Unable to create $DOCKER_PLUGIN_BUILDER builder. ERROR:  $createOutput")
            }
        }
    }
}