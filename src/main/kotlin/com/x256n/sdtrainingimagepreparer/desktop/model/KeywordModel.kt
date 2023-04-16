package com.x256n.sdtrainingimagepreparer.desktop.model

data class KeywordModel(
    val keyword: String,
    val usageCount: Int,
    var isAdded: Boolean = false
)