@file:OptIn(ExperimentalPathApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainingimagepreparer.desktop.common.BaseViewModel
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.model.KeywordModel
import com.x256n.sdtrainingimagepreparer.desktop.usecase.*
import org.koin.core.component.KoinComponent
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.system.exitProcess

@ExperimentalPathApi
class HomeViewModel(
    private val checkProject: CheckProjectUseCase,
    private val loadImageModels: LoadImageModelsUseCase,
    private val writeCaption: WriteCaptionUseCase,
    private val removeIncorrectThumbnails: RemoveIncorrectThumbnailsUseCase,
    private val extractCaptionKeywords: ExtractCaptionKeywordsUseCase,
    private val joinCaption: JoinCaptionUseCase,
    private val splitCaption: SplitCaptionUseCase,
    private val cropResizeImage: CropResizeImageUseCase,
    private val createNewAndMergeExistingCaptions: CreateNewAndMergeExistingCaptionsUseCase,
    private val configManager: ConfigManager,
    private val dropProject: DropProjectUseCase,
    private val deleteImage: DeleteImageUseCase,
    private val createCaptionIfNotExist: CreateCaptionIfNotExistUseCase,
    private val deleteCaption: DeleteCaptionUseCase,
) : BaseViewModel<HomeState>(emptyState = HomeState()), KoinComponent {

    // region Application events

    private fun initHomeScreen() {
        _log.info("HomeDisplayed")
        if (configManager.openLastProjectOnStart && configManager.lastProjectPath.isNotBlank()) {
            val projectDirectory = Paths.get(configManager.lastProjectPath)
            _state.value = state.value.copy(projectDirectory = projectDirectory)
            sendEvent(HomeEvent.LoadProject(projectDirectory))
        }
    }

    private fun exitApplication(event: HomeEvent.Exit) {
//        if (event.isConfirmed) {
        exitProcess(0)
//        }
    }

    // endregion

    // region Project events

    private suspend fun loadProject(event: HomeEvent.LoadProject) {
        state.value.projectDirectory?.let {
            // state.value.projectDirectory == null means there is no opened project
            sendEvent(HomeEvent.CloseProject)
        }

        val projectDirectory = event.projectDirectory

        _state.value = HomeState(
            isShowChooseProjectDirectoryDialog = false
        )
        checkProject(projectDirectory)

        removeIncorrectThumbnails(projectDirectory)

        val data = loadImageModels(projectDirectory)
        val dataIndex = if (state.value.dataIndex == -1) 0 else state.value.dataIndex

        createNewAndMergeExistingCaptions(projectDirectory, data)

        val keywordMap = data.map { extractCaptionKeywords(it) }
            .flatten()
            .toSet()
            .toList()

        val currentCaptionKeywords = extractCaptionKeywords(data[dataIndex])

        _state.value = state.value.copy(
            projectDirectory = projectDirectory,
            data = data,
            dataIndex = dataIndex,
            keywordList = keywordMap.map {
                it.copy(isAdded = currentCaptionKeywords.contains(it))
            },
            captionContent = joinCaption(currentCaptionKeywords.map { it.keyword })
        )

        configManager.lastProjectPath = projectDirectory.toAbsolutePath().normalize().toString()
    }

    private fun showOpenProjectDialog() {
        _state.value = state.value.copy(
            isShowChooseProjectDirectoryDialog = true
        )
    }

    private fun closeProject() {
        _state.value = HomeState()
    }

    private suspend fun closeAndDropProject() {
        state.value.projectDirectory?.let { projectDirectory ->
            sendEvent(HomeEvent.CloseProject)
            dropProject(projectDirectory)
        }
    }

    // endregion

    // region Image events

    private suspend fun deleteSelectedImage() {
        val selectedImageModel = state.value.selectedImageModel
        val dataIndex = state.value.dataIndex

        deleteImage(selectedImageModel)

        _state.value = state.value.copy(
            data = state.value.data.toMutableList().apply {
                this.removeAt(dataIndex)
            },
            dataIndex = if (dataIndex > state.value.data.lastIndex) state.value.data.lastIndex else dataIndex
        )
    }

    private fun changeImageSizeOnScreen(event: HomeEvent.ImageSizeChanged) {
        _state.value = state.value.copy(
            realImageSize = event.imageSize,
            imageScale = event.imageScale
        )
    }

    private suspend fun changeSelectedImage(event: HomeEvent.ImageSelected) {
        val index = event.index
        _log.debug("Selected image: index = $index, name = ${state.value.selectedImageModel.imagePath.name}")
        // Save current keywords to caption file
        val currentModelKeywordList = splitCaption(state.value.captionContent)
        if (currentModelKeywordList.isNotEmpty()) {
            writeCaption(state.value.selectedImageModel, currentModelKeywordList)
        }
        // Add missing keywords to right panel
        val actualRightPanelKeywordList = state.value.keywordList.map { it.copy(isAdded = false) }.toMutableList()
        actualRightPanelKeywordList.addAll(currentModelKeywordList.map { KeywordModel(it, 1) })

        val currentCaptionKeywords = extractCaptionKeywords(state.value.data[index])

        // Change selected image
        _state.value = state.value.copy(
            screenMode = ScreenMode.Default,
            dataIndex = index,
            captionContent = joinCaption(currentCaptionKeywords.map { it.keyword }),
            keywordList = actualRightPanelKeywordList.toSet().map {
                it.copy(isAdded = currentCaptionKeywords.contains(it))
            }
        )
    }

    private fun showNextImage() {
        sendEvent(HomeEvent.ImageSelected(if (state.value.dataIndex == state.value.data.lastIndex) 0 else state.value.dataIndex + 1))
    }

    private fun showPrevImage() {
        sendEvent(HomeEvent.ImageSelected(if (state.value.dataIndex == 0) state.value.data.lastIndex else state.value.dataIndex - 1))
    }

    // endregion

    // region Captions events

    private suspend fun createAllCaptions() {
        state.value.data.forEach { model ->
            createCaptionIfNotExist(model, emptyList())
        }
    }

    private suspend fun deleteAllCaptions(event: HomeEvent.DeleteAllCaptions) {
        state.value.data.forEach {
            deleteCaption(it, isDeleteOnlyEmpty = event.isDeleteOnlyEmpty)
        }
    }

    private fun changeCaptionContent(event: HomeEvent.CaptionContentChanged) {
        _state.value = state.value.copy(
            captionContent = event.value
        )
    }

    private suspend fun selectKeyword(event: HomeEvent.KeywordSelected) {
        _log.debug("Clicked keyword: ${event.keywordModel.keyword}")

        val activeModel = state.value[state.value.dataIndex]
        val captionList = extractCaptionKeywords(activeModel).toMutableList()

        if (event.keywordModel.isAdded) {
            captionList.removeIf { it.keyword == event.keywordModel.keyword }
        } else {
            captionList.add(event.keywordModel.copy(isAdded = true))
        }
        val keywordSet = captionList.map { it.keyword }
        writeCaption(activeModel, keywordSet)

        _state.value = state.value.copy(
            keywordList = state.value.keywordList.map {
                it.copy(isAdded = keywordSet.contains(it.keyword))
            },
            captionContent = joinCaption(keywordSet)
        )
    }

    // endregion

    // region Image tools events

    private fun toggleEditMode(event: HomeEvent.EditModeClicked) {
        _state.value = state.value.copy(
            screenMode = if (event.enable) ScreenMode.ResizeCrop else ScreenMode.Default,
            cropOffset = if (event.enable) state.value.cropOffset else Offset(0f, 0f),
            cropSize = if (event.enable) state.value.cropSize else Size(
                min(512f, state.value.realImageSize.width),
                min(512f, state.value.realImageSize.height)
            )
        )
    }

    private suspend fun cropResizeImage() {
        val imageScale = state.value.imageScale
        val cropOffset = state.value.cropOffset.copy(
            x = state.value.cropOffset.x,
            y = state.value.cropOffset.y,
        )
        val cropSize = state.value.cropSize.copy(
            width = state.value.cropSize.width,
            height = state.value.cropSize.height
        )

        _log.debug("cropResizeImage: offset = $cropOffset, size = $cropSize, scale = $imageScale")

        val imageModel = state.value.data[state.value.dataIndex]
        val newImageModel = cropResizeImage(
            imageModel,
            cropOffset,
            cropSize
        )
        _state.value = state.value.copy(
            data = state.value.data.map { if (it == imageModel) newImageModel else it },
            screenMode = ScreenMode.Default
        )
    }

    private fun changeAreaSize(event: HomeEvent.ChangeAreaToSize) {
        var x = state.value.cropOffset.x
        var y = state.value.cropOffset.y
        var width = event.targetSize
        var height = event.targetSize
        val imageWidth = state.value.realImageSize.width
        val imageHeight = state.value.realImageSize.height

        if (width > imageWidth) {
            x = 0f
            width = imageWidth
        } else if (x + width > imageWidth) {
            x = imageWidth - width
        }

        if (height > imageHeight) {
            y = 0f
            height = imageHeight
        } else if (y + height > imageHeight) {
            y = imageHeight - height
        }

        _state.value = state.value.copy(
            cropOffset = Offset(x, y),
            cropSize = Size(width, height)
        )
    }

    private fun changeCropRectangle(event: HomeEvent.CropRectChanged) {
        val imageScale = state.value.imageScale
        val realImageSize = state.value.realImageSize
        val cropRect = Rect(state.value.cropOffset, state.value.cropSize)
        val activeType = state.value.cropActiveType
        val isShiftPressed = event.isShiftPressed
        val offset = event.offset.copy(
            event.offset.x * imageScale,
            event.offset.y * imageScale
        )

        var cropAreaRect = when (activeType) {
            is ActiveType.LeftTop -> calculateTopBottomDiagonalOffsets(offset, isShiftPressed,
                isInsideImage = { offsetHorz, offsetVert ->
                    cropRect.left + offsetHorz > 0 && cropRect.top + offsetVert > 0
                },
                modifyRect = { offsetHorz, offsetVert ->
                    cropRect.copy(
                        left = cropRect.left + offsetHorz,
                        top = cropRect.top + offsetVert,
                    )
                })
            is ActiveType.RightTop -> calculateBottomTopDiagonalOffsets(offset, isShiftPressed,
                isInsideImage = { offsetHorz, offsetVert ->
                    cropRect.top + offsetVert > 0 && cropRect.right + offsetHorz < realImageSize.width
                },
                modifyRect = { offsetHorz, offsetVert ->
                    cropRect.copy(
                        top = cropRect.top + offsetVert,
                        right = cropRect.right + offsetHorz,
                    )
                })
            is ActiveType.RightBottom -> calculateTopBottomDiagonalOffsets(offset, isShiftPressed,
                isInsideImage = { offsetHorz, offsetVert ->
                    cropRect.right + offsetHorz < realImageSize.width && cropRect.bottom + offsetVert < realImageSize.height
                },
                modifyRect = { offsetHorz, offsetVert ->
                    cropRect.copy(
                        right = cropRect.right + offsetHorz,
                        bottom = cropRect.bottom + offsetVert,
                    )
                })
            is ActiveType.LeftBottom -> calculateBottomTopDiagonalOffsets(offset, isShiftPressed,
                isInsideImage = { offsetHorz, offsetVert ->
                    cropRect.left + offsetHorz > 0 && cropRect.bottom + offsetVert < realImageSize.height
                },
                modifyRect = { offsetHorz, offsetVert ->
                    cropRect.copy(
                        left = cropRect.left + offsetHorz,
                        bottom = cropRect.top + cropRect.height + offsetVert,
                    )
                })
            is ActiveType.Center -> {
                cropRect.copy(
                    left = cropRect.left + offset.x,
                    top = cropRect.top + offset.y,
                    right = cropRect.right + offset.x,
                    bottom = cropRect.bottom + offset.y,
                )
            }
            else -> {
                cropRect
            }
        }

        // Prevent area to be out of image
        cropAreaRect = keepAreaInsideImage(cropAreaRect, realImageSize)

        val scaledMinSize = 25 * imageScale

        // Prevent left corner is on right of right corner and so on
        cropAreaRect = if (cropAreaRect.right - cropAreaRect.left < scaledMinSize) { // left must not be more right
            if (activeType == ActiveType.LeftTop || activeType == ActiveType.LeftBottom) {
                cropAreaRect.copy(left = cropAreaRect.right - scaledMinSize)
            } else {
                cropAreaRect.copy(right = cropAreaRect.left + scaledMinSize)
            }
        } else cropAreaRect

        cropAreaRect = if (cropAreaRect.bottom - cropAreaRect.top < scaledMinSize) { // top must not be more bottom
            if (activeType == ActiveType.LeftTop || activeType == ActiveType.RightTop) {
                cropAreaRect.copy(top = cropAreaRect.bottom - scaledMinSize)
            } else {
                cropAreaRect.copy(bottom = cropAreaRect.top + scaledMinSize)
            }
        } else cropAreaRect

        _state.value = state.value.copy(
            cropOffset = cropAreaRect.topLeft,
            cropSize = cropAreaRect.size
        )
    }

    private fun calculateTopBottomDiagonalOffsets(
        offset: Offset, isShiftPressed: Boolean,
        isInsideImage: (offsetHorz: Float, offsetVert: Float) -> Boolean,
        modifyRect: (offsetHorz: Float, offsetVert: Float) -> Rect
    ): Rect {
        var offsetHorz = offset.x
        var offsetVert = offset.y
        if (!isShiftPressed) {
            val vector = (abs(offset.x) + abs(offset.y)) / 2
            if (offset.x < 0 || offset.y < 0) {
                offsetHorz = -vector
                offsetVert = -vector
            } else {
                offsetHorz = vector
                offsetVert = vector
            }
        }
        return if (isInsideImage(offsetHorz, offsetVert)) {
            modifyRect(offsetHorz, offsetVert)
        } else {
            // Keep inside image
            modifyRect(0f, 0f)
        }
    }

    private fun calculateBottomTopDiagonalOffsets(
        offset: Offset, isShiftPressed: Boolean,
        isInsideImage: (offsetHorz: Float, offsetVert: Float) -> Boolean,
        modifyRect: (offsetHorz: Float, offsetVert: Float) -> Rect
    ): Rect {
        var offsetHorz = offset.x
        var offsetVert = offset.y
        if (!isShiftPressed) {
            val vector = (abs(offset.x) + abs(offset.y)) / 2
            if (offset.x < 0) {
                offsetHorz = -vector
                offsetVert = vector
            } else {
                offsetHorz = vector
                offsetVert = -vector
            }
        }
        return if (isInsideImage(offsetHorz, offsetVert)) {
            modifyRect(offsetHorz, offsetVert)
        } else {
            // Keep inside image
            modifyRect(0f, 0f)
        }
    }

    private fun keepAreaInsideImage(
        cropAreaRect: Rect,
        realImageSize: Size
    ): Rect { // Check!! is this method still required
        var fixedRect = cropAreaRect
        // Prevent area to be out of image
        if (fixedRect.left < 0) {
            fixedRect = fixedRect.copy(
                left = 0f,
                right = min(fixedRect.width, realImageSize.width)
            )
        }
        if (fixedRect.top < 0) {
            fixedRect = fixedRect.copy(
                top = 0f,
                bottom = min(fixedRect.height, realImageSize.height)
            )
        }
        if (fixedRect.right > realImageSize.width) {
            fixedRect = fixedRect.copy(
                right = realImageSize.width,
                left = max(0f, realImageSize.width - fixedRect.width)
            )
        }
        if (fixedRect.bottom > realImageSize.height) {
            fixedRect = fixedRect.copy(
                bottom = realImageSize.height,
                top = max(0f, realImageSize.height - fixedRect.height)
            )
        }

        return fixedRect
    }

    private fun changeCropActiveType(event: HomeEvent.CropActiveTypeChanged) {
        val rectOffset = state.value.cropOffset
        val rectSize = state.value.cropSize
        val imageScale = state.value.imageScale

        val cropActiveType = if (event.position != null) {
            val scaledAreaRect = Rect(
                left = rectOffset.x / imageScale,
                top = rectOffset.y / imageScale,
                right = (rectOffset.x + rectSize.width) / imageScale,
                bottom = (rectOffset.y + rectSize.height) / imageScale,
            )
            ActiveType.getActiveType(
                event.position,
                scaledAreaRect
            )
        } else ActiveType.None

        _log.debug("cropActiveType = $cropActiveType")
        _state.value = state.value.copy(cropActiveType = cropActiveType)
    }

    // endregion

    // region Keyboard events

    private fun enterButtonPressed() {
        if (state.value.screenMode == ScreenMode.ResizeCrop) {
            sendEvent(HomeEvent.CropApplyClicked)
        }
    }

    private fun escButtonPressed() {
        if (state.value.screenMode == ScreenMode.ResizeCrop) {
            _state.value = state.value.copy(
                screenMode = ScreenMode.Default,
                cropOffset = Offset(0f, 0f),
                cropSize = Size(512f, 512f),
                cropActiveType = ActiveType.None,
            )
        }
    }

    private fun deleteButtonPressed() {
        // Deleting image will be processed in screen file. When screenMode is Default event will not be sent here.
    }

    // endregion


    /**
     * This method was made this way just to make 'when' smaller so that IDE will not show warning about too deprecated method
     */
    override suspend fun onEvent(event: HomeEvent) {
        when (event) {
            // region Application events
            is HomeEvent.HomeDisplayed, is HomeEvent.Exit -> {
                onApplicationEvent(event)
            }
            // endregion

            // region Project events
            is HomeEvent.LoadProject, is HomeEvent.OpenProject, is HomeEvent.CloseProject, is HomeEvent.DropProject -> {
                onProjectEvent(event)
            }
            // endregion

            // region Image events
            is HomeEvent.DeleteImage, is HomeEvent.ImageSizeChanged, is HomeEvent.ImageSelected, is HomeEvent.ShowNextImage, is HomeEvent.ShowPrevImage -> {
                onImageEvent(event)
            }
            // endregion

            // region Captions events
            is HomeEvent.CreateAllCaptions, is HomeEvent.DeleteAllCaptions, is HomeEvent.CaptionContentChanged, is HomeEvent.KeywordSelected -> {
                onCaptionsEvent(event)
            }
            // endregion

            // region Toolbar events
            is HomeEvent.EditModeClicked, is HomeEvent.CropApplyClicked, is HomeEvent.ChangeAreaToSize, is HomeEvent.CropRectChanged, is HomeEvent.CropActiveTypeChanged -> {
                onToolbarEvent(event)
            }
            // endregion

            // region Keyboard
            is HomeEvent.EnterPressed, is HomeEvent.EscPressed, is HomeEvent.DeletePressed -> {
                onKeyboardEvent(event)
            }
            // endregion
            else -> {
                TODO("Not implemented: $event")
            }
        }
    }

    private fun onApplicationEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.HomeDisplayed -> initHomeScreen()
            is HomeEvent.Exit -> exitApplication(event)
            else -> throw DisplayableException("Unexpected application state: c92a533424d4($event)")
        }
    }

    private suspend fun onProjectEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadProject -> loadProject(event)
            is HomeEvent.OpenProject -> showOpenProjectDialog()
            is HomeEvent.CloseProject -> closeProject()
            is HomeEvent.DropProject -> closeAndDropProject()
            else -> throw DisplayableException("Unexpected application state: 24cf91708f36($event)")
        }
    }

    private suspend fun onImageEvent(event: HomeEvent) {

        when (event) {
            is HomeEvent.DeleteImage -> deleteSelectedImage()
            is HomeEvent.ImageSizeChanged -> changeImageSizeOnScreen(event)
            is HomeEvent.ImageSelected -> changeSelectedImage(event)
            is HomeEvent.ShowNextImage -> showNextImage()
            is HomeEvent.ShowPrevImage -> showPrevImage()
            else -> throw DisplayableException("Unexpected application state: c92a533424d4($event)")
        }
    }

    private suspend fun onCaptionsEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.CreateAllCaptions -> createAllCaptions()
            is HomeEvent.DeleteAllCaptions -> deleteAllCaptions(event)
            is HomeEvent.CaptionContentChanged -> changeCaptionContent(event)
            is HomeEvent.KeywordSelected -> selectKeyword(event)
            else -> throw DisplayableException("Unexpected application state: 44f67cf5f537($event)")
        }
    }

    private suspend fun onToolbarEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.EditModeClicked -> toggleEditMode(event)
            is HomeEvent.CropApplyClicked -> cropResizeImage()
            is HomeEvent.ChangeAreaToSize -> changeAreaSize(event)
            is HomeEvent.CropRectChanged -> changeCropRectangle(event)
            is HomeEvent.CropActiveTypeChanged -> changeCropActiveType(event)
            else -> throw DisplayableException("Unexpected application state: 7c0e742c3764($event)")
        }
    }

    private fun onKeyboardEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.EnterPressed -> enterButtonPressed()
            is HomeEvent.EscPressed -> escButtonPressed()
            is HomeEvent.DeletePressed -> deleteButtonPressed()
            else -> throw DisplayableException("Unexpected application state: bbceb2ae77ac($event)")
        }
    }

    override fun showProgressBar() {
        _state.value = state.value.copy(isLoading = true)
    }

    override fun hideProgressBar() {
        _state.value = state.value.copy(isLoading = false)
    }

    override fun showError(message: String) {
        _state.value = state.value.copy(errorMessage = message)
    }

    override fun hideError() {
        _state.value = state.value.copy(errorMessage = null)
    }
}