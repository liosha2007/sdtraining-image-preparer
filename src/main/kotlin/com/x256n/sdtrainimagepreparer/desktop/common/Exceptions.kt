package com.x256n.sdtrainimagepreparer.desktop.common

import java.nio.file.Path

open class SDTrainImagePreparerException(message: String, throwable: Throwable? = null) : RuntimeException(message, throwable)

open class DisplayableException(message: String) : SDTrainImagePreparerException(message)

data class ProjectDirectoryDoesNotExist(val path: Path) : SDTrainImagePreparerException("Project directory does not exist: '$path'")

data class ProjectAlreadyExist(val path: Path) : SDTrainImagePreparerException("Project already exist in the directory: '$path'")

data class ProjectConfigNotFoundException(val path: Path) : SDTrainImagePreparerException("Project config does not exist: '$path'")

data class CantDeleteProjectException(val path: Path) : SDTrainImagePreparerException("Can't delete project: '$path'")