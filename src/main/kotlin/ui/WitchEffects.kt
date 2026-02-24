package ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun WitchLoadingEffect(enabled: Boolean = true) {
    if (!enabled) return

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current

        // Pobieramy wymiary kontenera w pikselach
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // 4 Wiedźmy z różnymi parametrami
        FlyingWitch(widthPx, heightPx, speedMillis = 4000, delayMillis = 0, verticalBias = 0.15f, sizeDp = 120)
        FlyingWitch(widthPx, heightPx, speedMillis = 5500, delayMillis = 1500, verticalBias = 0.30f, sizeDp = 80)
        FlyingWitch(widthPx, heightPx, speedMillis = 3500, delayMillis = 800, verticalBias = 0.70f, sizeDp = 100)
        FlyingWitch(widthPx, heightPx, speedMillis = 4800, delayMillis = 2500, verticalBias = 0.85f, sizeDp = 75)
    }
}

@Composable
private fun BoxWithConstraintsScope.FlyingWitch(
    screenWidthPx: Float,
    screenHeightPx: Float,
    speedMillis: Int,
    delayMillis: Int,
    verticalBias: Float,
    sizeDp: Int
) {
    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition()

    // Konwersja rozmiaru Dp na piksele przed animacją
    val sizePx = with(density) { sizeDp.dp.toPx() }

    // Animacja od lewej do prawej
    val xOffsetPx by infiniteTransition.animateFloat(
        initialValue = -sizePx,
        targetValue = screenWidthPx + sizePx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = speedMillis, delayMillis = delayMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Przeliczenie offsetu X na Dp, aby użyć go w Modifier.offset
    val xOffsetDp = with(density) { xOffsetPx.toDp() }

    Image(
        painter = painterResource("drawable/witch.png"),
        contentDescription = null,
        modifier = Modifier
            .size(sizeDp.dp)
            .align(Alignment.TopStart)
            .offset(x = xOffsetDp)
            .graphicsLayer {
                // Obliczamy pozycję Y na podstawie procentu wysokości okna
                // verticalBias 0.5f to idealny środek
                translationY = (screenHeightPx * verticalBias) - (sizePx / 2)
                alpha = 0.85f
            }
    )
}