@file:OptIn(ExperimentalNavigationApi::class, ExperimentalNavigationApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.navigation

import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.StackDuplicateContentStrategy
import com.chrynan.navigation.compose.ComposeNavigatorByKey
import com.chrynan.navigation.compose.goTo


interface INavigator<D, S> {
    val currentKey: D
    fun goTo(key: D)
    fun goTo(key: D, strategy: StackStrategy<S>)
}

class StackStrategy<S>(val value: S)

class NothingNavigator<D, S>(
    override val currentKey: D
) : INavigator<D, S> {
    override fun goTo(key: D, strategy: StackStrategy<S>) {
    }

    override fun goTo(key: D) {
    }
}

class ChrynanNavigator(
    private val navigator: ComposeNavigatorByKey<Destinations, Destinations>?
) :
    INavigator<Destinations, StackDuplicateContentStrategy> {
    override fun goTo(key: Destinations, strategy: StackStrategy<StackDuplicateContentStrategy>) {
        navigator?.goTo(key, strategy.value)
    }

    override fun goTo(key: Destinations) {
        navigator?.goTo(key)
    }

    override val currentKey: Destinations
        get() = navigator?.currentKey ?: Destinations.Home()
}