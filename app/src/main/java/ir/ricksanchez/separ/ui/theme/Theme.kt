package ir.ricksanchez.separ.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ShieldGreen,
    secondary = WarningAmber,
    error = DangerRed,
    background = DarkBackground,
    surface = DarkSurface
)

@Composable
fun SeparTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
