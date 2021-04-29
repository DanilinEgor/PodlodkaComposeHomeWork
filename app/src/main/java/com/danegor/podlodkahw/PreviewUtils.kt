package com.danegor.podlodkahw

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.danegor.podlodkahw.ui.theme.PodlodkaHWTheme

@Composable
internal fun ThemedPreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    PodlodkaHWTheme(darkTheme = darkTheme) {
        Surface {
            content()
        }
    }
}
