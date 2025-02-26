package com.fussionlabs.gradle.docker.tasks

import com.fussionlabs.gradle.docker.utils.PluginUtils.dockerExt
import java.io.ByteArrayInputStream

open class DockerLogin: DockerTask() {

    override fun exec() {
        val username = project.dockerExt.username
        val password = project.dockerExt.password

        dockerTaskArgs.addAll(listOf("login", "--username", username, "--password-stdin"))
        project.dockerExt.registry.takeIf { it.isNotEmpty() }?.let { registry ->
            dockerTaskArgs.add(registry)
        }

        standardInput = ByteArrayInputStream(password.toByteArray())

        super.exec()
    }
}