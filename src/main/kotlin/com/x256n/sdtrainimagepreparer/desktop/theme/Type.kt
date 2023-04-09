package com.x256n.sdtrainimagepreparer.desktop.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Blue
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        color = BlueLight
    )
)