package com.x256n.sdtrainimagepreparer.desktop.common

import java.nio.file.Path

open class SDTrainImagePreparerException(message: String, throwable: Throwable? = null) : RuntimeException(message, throwable)

open class DisplayableException(message: String) : SDTrainImagePreparerException(message)

data class ProjectDirectoryDoesNotExist(val path: Path) : SDTrainImagePreparerException("Project directory does not exist: '$path'")

data class ProjectAlreadyExist(val path: Path) : SDTrainImagePreparerException("Project already exist in the directory: '$path'")

data class ProjectConfigNotFoundException(val path: Path) : SDTrainImagePreparerException("Project config does not exist: '$path'")

data class CantDeleteProjectException(val path: Path) : SDTrainImagePreparerException("Can't delete project: '$path'")

data class NotAProjectException(val path: Path) : SDTrainImagePreparerException("The directory is not a project: '$path'")

data class CantCreateProjectException(val path: Path) : SDTrainImagePreparerException("Can't create project: '$path'")

data class CantSaveProjectException(val path: Path) : SDTrainImagePreparerException("Can't save project: '$path'")

data class CantLoadProjectException(val path: Path) : SDTrainImagePreparerException("Can't load project: '$path'")

data class CantCreateCaptionException(val path: Path) : SDTrainImagePreparerException("Can't create caption file: '$path'")

data class CantSaveCaptionException(val path: Path) : SDTrainImagePreparerException("Can't save caption file: '$path'")

data class CantLoadCaptionException(val path: Path) : SDTrainImagePreparerException("Can't load caption file: '$path'")