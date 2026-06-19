package fr.utbm.sy43.pilulito.ui.theme


import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class ThemeState(initialDark: Boolean = false) {
    var isDark by mutableStateOf(initialDark)
}

val LocalThemeState = compositionLocalOf { ThemeState() }