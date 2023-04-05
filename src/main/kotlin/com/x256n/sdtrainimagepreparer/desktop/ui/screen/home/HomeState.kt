package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import com.x256n.sdtrainimagepreparer.desktop.model.SampleModel
import kotlinx.coroutines.Job

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: List<Window> = emptyList(),
    val selectedIndex: Int = 0,
) {
//    val selectedItem
//        get() =
//            storage.regexs[selectedIndex]
//
//    fun createItem() =
//        RegexModel(
//            order = order,
//            name = name,
//            regex = regex,
//            replacement = replacement,
//            caseInsensitive = caseInsensitive,
//            dotAll = dotAll,
//            multiline = multiline,
//            enabled = true
//        )
//
//    val hasData get() = storage.regexs.isNotEmpty()
}

data class Window(
    var character: SampleModel,
    var job: Job? = null,
    val locals: List<String>? = null,
    val ships: List<Ship>? = null
)

sealed class Distance(val value: Double) {
    class Ae(value: Double) : Distance(value)
    class Km(value: Double) : Distance(value)

    companion object {
        val None = Km(-1.0)
    }
}

data class Ship(val type: String, val distance: Distance) {
    override fun toString(): String = "$type [${distance.value}]"
}