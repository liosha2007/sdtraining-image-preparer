package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainimagepreparer.desktop.model.KeywordModel
import java.nio.file.Path

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val projectDirectory: Path? = null,
    val isShowChooseProjectDirectoryDialog: Boolean = false,
    val data: List<ImageModel> = emptyList(),
    val dataIndex: Int = -1,
    val captionContent: String = "",
    val keywordList: List<KeywordModel> = emptyList(),
    val isEditMode: Boolean = false,
    val mainImageSize: Size = Size(0f, 0f),
    val cropOffset: Offset = Offset(0f, 0f),
    val cropSize: Size = Size(512f, 512f)
) {
    operator fun get(index: Int): ImageModel {
        return data[index]
    }

    val hasData get() = data.isNotEmpty()

    val selectedImageModel get() = data[dataIndex]

    val hasKeywords get() = keywordList.isNotEmpty()

    val statusText
        get() =
            if (hasData) "${this[dataIndex].imageSize.width.toInt()} x ${this[dataIndex].imageSize.height.toInt()} - ${this[dataIndex].imageName}" else ""

    val isProjectLoaded get() = projectDirectory != null

    fun addMissingKeywords(keywordsSet: List<String>, isAdded: Boolean = true): List<KeywordModel> {
        val keywordsStringSet = keywordList.map { it.keyword }
        val filteredKeywordsSet = keywordsSet.filter { !keywordsStringSet.contains(it) }
        return keywordList.toMutableSet().apply {
            addAll(filteredKeywordsSet.map {
                KeywordModel(
                    keyword = it,
                    usageCount = 1,
                    isAdded = isAdded
                )
            })
        }.toList()
    }
}