package com.example.hapticapptest.ui.theme

import android.os.Build
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun HapticAppTestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Immutable
internal object DefaultRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = BetterTheme.colors.backgroundRipple

    @Composable
    override fun rippleAlpha() = RippleAlpha(
        pressedAlpha = 0.05f,
        focusedAlpha = 0.04f,
        draggedAlpha = 0.05f,
        hoveredAlpha = 0.03f
    )
}

internal val darkColors = lightColors

@Composable
fun BetterTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val rememberedColors = remember {
        if (darkTheme) darkColors else lightColors
    }

    MaterialTheme {
        val rippleIndication = rememberRipple()
        CompositionLocalProvider(
            LocalColors provides rememberedColors,
            LocalIndication provides rippleIndication,
            LocalRippleTheme provides DefaultRippleTheme,
        ) {
            content()
        }
    }
}

/**
 * Contains getters to access the current theme values
 */
object BetterTheme {
    /**
     * Provides current [Colors] according to the current color scheme
     */
    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    /* */
    /**
     * Provides current [Typography]
     *//*
    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current*/
}

internal val LocalColors = staticCompositionLocalOf { lightColors }

//internal val LocalTypography = staticCompositionLocalOf { defaultTypography }