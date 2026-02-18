package com.nbahub.platform.design

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

val NbaBlue = Color(0xFF1D428A)
val NbaRed = Color(0xFFC8102E)
val NbaWhite = Color(0xFFFFFFFF)

// Semantic extension colors for app-wide use
val ColorScheme.favoriteContainer: Color
    get() = Color(0xFF1565C0)

val ColorScheme.onFavoriteContainer: Color
    get() = Color.White

val ColorScheme.onFavoriteContainerVariant: Color
    get() = Color.White.copy(alpha = 0.7f)

val ColorScheme.favoriteAccent: Color
    get() = Color(0xFFFFD600)

val ColorScheme.statusLive: Color
    get() = Color(0xFFD32F2F)

val ColorScheme.statusFinal: Color
    get() = Color(0xFF616161)

val ColorScheme.onStatusBadge: Color
    get() = Color.White

val LightPrimary = NbaBlue
val LightOnPrimary = NbaWhite
val LightSecondary = NbaRed
val LightBackground = Color(0xFFF2F2F2)
val LightSurface = NbaWhite
val LightOnBackground = Color(0xFF1C1B1F)
val LightOnSurface = Color(0xFF1C1B1F)
val LightSurfaceVariant = Color(0xFFE8E8E8)
val LightOnSurfaceVariant = Color(0xFF757575)
val LightOutline = Color(0xFFD5D5D5)
val LightOutlineVariant = Color(0xFFE0E0E0)
val LightInverseSurface = Color(0xFFE0E0E0)
val LightInverseOnSurface = Color(0xFF1C1B1F)

val DarkPrimary = Color(0xFF5B8CC5)
val DarkOnPrimary = Color(0xFFFFFFFF)
val DarkSecondary = Color(0xFFFF6B6B)
val DarkOnSecondary = Color(0xFFFFFFFF)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnBackground = Color(0xFFE6E1E5)
val DarkOnSurface = Color(0xFFE6E1E5)
val DarkSurfaceVariant = Color(0xFF2C2C2C)
val DarkOnSurfaceVariant = Color(0xFFB0B0B0)
val DarkOutline = Color(0xFF444444)
val DarkOutlineVariant = Color(0xFF333333)
val DarkInverseSurface = Color(0xFF3A3A3A)
val DarkInverseOnSurface = Color(0xFFE6E1E5)
