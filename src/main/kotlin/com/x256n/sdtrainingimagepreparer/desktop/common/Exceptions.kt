package com.x256n.sdtrainingimagepreparer.desktop.common

import java.nio.file.Path

open class sdtrainingimagepreparerException(message: String, throwable: Throwable? = null) : RuntimeException(message, throwable)

open class DisplayableException(message: String) : sdtrainingimagepreparerException(message)

data class ProjectDirectoryDoesNotExist(val path: Path) : sdtrainingimagepreparerException("Project directory does not exist: '$path'")

data class ProjectAlreadyExist(val path: Path) : sdtrainingimagepreparerException("Project already exist in the directory: '$path'")

data class ProjectConfigNotFoundException(val path: Path) : sdtrainingimagepreparerException("Project config does not exist: '$path'")

data class CantDeleteProjectException(val path: Path) : sdtrainingimagepreparerException("Can't delete project: '$path'")

data class NotAProjectException(val path: Path) : sdtrainingimagepreparerException("The directory is not a project: '$path'")

data class CantCreateProjectException(val path: Path) : sdtrainingimagepreparerException("Can't create project: '$path'")

data class CantSaveProjectException(val path: Path) : sdtrainingimagepreparerException("Can't save project: '$path'")

data class CantLoadProjectException(val path: Path) : sdtrainingimagepreparerException("Can't load project: '$path'")

data class CantCreateCaptionException(val path: Path) : sdtrainingimagepreparerException("Can't create caption file: '$path'")

data class CantSaveCaptionException(val path: Path) : sdtrainingimagepreparerException("Can't save caption file: '$path'")

data class CantLoadCaptionException(val path: Path) : sdtrainingimagepreparerException("Can't load caption file: '$path'")