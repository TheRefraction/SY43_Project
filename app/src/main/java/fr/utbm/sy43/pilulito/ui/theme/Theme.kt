package fr.utbm.sy43.pilulito.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary      = Color(0xFFFF6C00),
    onPrimary    = Color.White,
    background   = Color(0xFF1A1A1A),
    onBackground = Color(0xFFEEEEEE),
    surface      = Color(0xFF2A2A2A),
    onSurface    = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFFCCCCCC)
)

private val LightColorScheme = lightColorScheme(
    primary      = Color(0xFFFF6C00),
    onPrimary    = Color.White,
    background   = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    surface      = Color(0xFFFAFAFA),
    onSurface    = Color(0xFF1C1B1F),
    onSurfaceVariant = Color(0xFF666666)
)

@Composable
fun SenPosTheme(
    themeState: ThemeState = LocalThemeState.current,
    content: @Composable () -> Unit
) {
    val colorScheme = if (themeState.isDark) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalThemeState provides themeState) {
        MaterialTheme(
            colorScheme = colorScheme,
            content     = content
        )
    }
}