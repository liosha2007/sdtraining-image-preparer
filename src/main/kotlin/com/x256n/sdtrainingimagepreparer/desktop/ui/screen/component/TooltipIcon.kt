@file:OptIn(ExperimentalFoundationApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun TooltipIcon(
    modifier: Modifier = Modifier,
    iconResource: String,
    tooltip: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TooltipArea(
        tooltip = {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 5.dp,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Text(
                    text = tooltip,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(3.dp),
                )
            }
        }
    ) {
        Box(
            modifier = modifier
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
            CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
                Icon(
                    painter = painterResource(iconResource),
                    contentDescription = tooltip
                )
            }
        }
    }
}