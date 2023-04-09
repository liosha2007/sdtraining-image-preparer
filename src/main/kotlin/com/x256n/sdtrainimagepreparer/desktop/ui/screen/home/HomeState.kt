package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainimagepreparer.desktop.model.KeywordModel
import java.nio.file.Path

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val projectDirectory: Path? = null,
    val isOpenProject: Boolean = false,
    val data: List<ImageModel> = emptyList(),
    val dataIndex: Int = -1,
    val captionContent: String = "",
    val keywordList: List<KeywordModel> = emptyList(),
) {
    operator fun get(index: Int): ImageModel {
        return data[index]
    }

    val hasData get() = data.isNotEmpty()

    val hasKeywords get() = keywordList.isNotEmpty()

    val statusText
        get() =
            if (hasData) "${this[dataIndex].imageWidth} x ${this[dataIndex].imageHeight} - ${this[dataIndex].imageName}" else ""

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