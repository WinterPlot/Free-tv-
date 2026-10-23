package online.userlabs.freetv

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF0B1020)
val NavySurface = Color(0xFF151B31)
val NavyHigh = Color(0xFF1E2642)
val Accent = Color(0xFF6EA8FF)
val Pink = Color(0xFFFF6B8B)
val Muted = Color(0xFF9AA4BF)

private val Scheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color(0xFF06142E),
    secondary = Pink,
    background = Navy,
    onBackground = Color(0xFFEFF2FB),
    surface = NavySurface,
    onSurface = Color(0xFFEFF2FB),
    surfaceVariant = NavyHigh,
    onSurfaceVariant = Muted,
    surfaceContainer = NavySurface,
    surfaceContainerHigh = NavyHigh,
    outline = Color(0xFF39425F),
)

@Composable
fun FreeTvTheme(content: @Composable () -> Unit) {
    // The app is dark-only: it is a TV app and the logos sit on light tiles.
    MaterialTheme(colorScheme = Scheme, content = content)
}
