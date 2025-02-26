# Docker Plugin
## Overview
The `docker-plugin` is a Gradle plugin created to simplify the process of building and pushing Docker images. It calls the Docker CLI directly, supports multiplatform builds 
and provides the ability to define custom Docker tasks.

## Usage
To use the `docker-plugin`, include it in your Gradle project by adding the following to your `build.gradle` file:

```kotlin
plugins {
    id("com.fussionlabs.gradle.docker-plugin") version "0.1.0"
}
```

## Configuration
The Docker build and push can be configured via the following options:

| Option           | Description                                                                     | Default Value                            |
|------------------|---------------------------------------------------------------------------------|------------------------------------------|
| `dockerFilePath` | Path to the Dockerfile used for building the image                              | `""` (defaults to the `rootProject` dir) |
| `registry`       | Docker registry where the image will be pushed                                  | `"hub.docker.com"` (optional)            |
| `repository`     | Docker repository where the image will be pushed                                | `""` (required)                          |
| `platforms`      | List of platforms for multiplatform builds (e.g., `linux/amd64`, `linux/arm64`) | `["linux/amd64"]`                        |
| `buildArgs`      | Map of build arguments to be passed to the Docker build command                 | `{}` (empty map)                         |
| `tags`           | List of tags to assign to the Docker image                                      | `[]` (empty list)                        |
| `applyLatestTag` | Boolean indicating if the `latest` tag should be automatically applied          | `true`                                   |
| `username`       | Docker username (can be set via `DOCKER_USERNAME` environment variable)         | `""` (required for pushing images)       |
| `password`       | Docker password (can be set via `DOCKER_PASSWORD` environment variable)         | `""` (required for pushing images)       |


## Default Tasks

```
Docker tasks
------------
dockerBuild  - Builds the Docker image(s) defined in the configuration. Supports multiplatform builds and custom build arguments.
dockerLogin  - Logs into the Docker registry using the provided username and password from the environment or configuration.
dockerPush   - Pushes the built Docker image(s) to the specified registry/repository, applying any specified tags and the `latest` tag if enabled.

```

## Example Usage

```kotlin
dockerPlugin {
    repository = "my-docker-repo/my-app"
    platforms = mutableListOf("linux/amd64", "linux/arm64")
    buildArgs = mutableMapOf("ARG_NAME" to "value")
    tags = mutableListOf("v1.0.0", "stable")
}
```

## Custom Tasks
In addition to the default tasks, you can create custom Docker tasks for basically any docker command:
```kotlin
tasks.register("dockerVersion", com.fussionlabs.gradle.docker.tasks.DockerTask::class.java) {
    dockerTaskArgs = mutableListOf("version")
}
```

## Authentication
Docker authentication can be managed via environment variables:

- `DOCKER_USERNAME`: Your Docker username
- `DOCKER_PASSWORD`: Your Docker password

These credentials are automatically picked up by the plugin when pushing images to a registry/repository.
