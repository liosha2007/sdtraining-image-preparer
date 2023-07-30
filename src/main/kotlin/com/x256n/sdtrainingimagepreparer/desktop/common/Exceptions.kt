package com.x256n.sdtrainingimagepreparer.desktop.common

import java.nio.file.Path

open class SDTrainingImagePreparerException(message: String, throwable: Throwable? = null) : RuntimeException(message, throwable)

open class DisplayableException(message: String) : SDTrainingImagePreparerException(message)

data class ProjectDirectoryDoesNotExist(val path: Path) : SDTrainingImagePreparerException("Project directory does not exist: '$path'")

data class ProjectAlreadyExist(val path: Path) : SDTrainingImagePreparerException("Project already exist in the directory: '$path'")

data class ProjectConfigNotFoundException(val path: Path) : SDTrainingImagePreparerException("Project config does not exist: '$path'")

data class CantDeleteProjectException(val path: Path) : SDTrainingImagePreparerException("Can't delete project: '$path'")

data class NotAProjectException(val path: Path) : SDTrainingImagePreparerException("The directory is not a project: '$path'")

data class CantCreateProjectException(val path: Path) : SDTrainingImagePreparerException("Can't create project: '$path'")

data class CantSaveProjectException(val path: Path) : SDTrainingImagePreparerException("Can't save project: '$path'")

data class CantLoadProjectException(val path: Path) : SDTrainingImagePreparerException("Can't load project: '$path'")

data class CantCreateCaptionException(val path: Path) : SDTrainingImagePreparerException("Can't create caption file: '$path'")

data class CantSaveCaptionException(val path: Path) : SDTrainingImagePreparerException("Can't save caption file: '$path'")

data class CantLoadCaptionException(val path: Path) : SDTrainingImagePreparerException("Can't load caption file: '$path'")