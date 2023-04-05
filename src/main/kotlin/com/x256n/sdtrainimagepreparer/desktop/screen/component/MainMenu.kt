package com.x256n.sdtrainimagepreparer.desktop.screen.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.MenuBarScope

@Composable
fun MenuBarScope.MainMenu() = Menu("File") {
    Item(
        "Reset",
        mnemonic = 'R',
//        shortcut = KeyShortcut(Key.R, ctrl = true),
        onClick = { println("Reset") }
    )
    CheckboxItem(
        "Advanced settings",
        mnemonic = 'A',
        checked = true,
        onCheckedChange = { /*isAdvancedSettings = !isAdvancedSettings*/ }
    )
    Menu("Theme") {
        Item(
            "Item 1",
//        shortcut = KeyShortcut(Key.R, ctrl = true),
            onClick = { println("Item 1") }
        )
        Item(
            "Item 2",
//        shortcut = KeyShortcut(Key.R, ctrl = true),
            onClick = { println("Item 2") }
        )
//        RadioButtonItem(
//            "Light",
//            mnemonic = 'L',
//            icon = ColorCircle(Color.LightGray),
//            selected = theme == Theme.Light,
//            onClick = { theme = Theme.Light }
//        )
//        RadioButtonItem(
//            "Dark",
//            mnemonic = 'D',
//            icon = ColorCircle(Color.DarkGray),
//            selected = theme == Theme.Dark,
//            onClick = { theme = Theme.Dark }
//        )
    }
    Item(
        "Exit",
        mnemonic = 'Q',
//        shortcut = KeyShortcut(Key.R, ctrl = true),
        onClick = { println("Exit") }
    )
}