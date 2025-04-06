package com.example.smartirrigationonlyread

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun WaterWaveWithBubbles(
    modifier: Modifier = Modifier,
    waveColor: Color = Color(0xFF00BCD4),
    bubbleColor: Color = Color.White,
    amplitude: Float = 30f,
    wavelength: Float = 400f,
    speed: Float = 0.6f
) {
    val phase = remember { Animatable(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "bubbles")

    // Create a list of animated bubbles
    val bubbles = remember {
        List(30) {
            Bubble(
                x = Random.nextFloat(),
                size = Random.nextFloat() * 20f + 4f,
                speed = Random.nextFloat() * 20f + 30f,
                alpha = Random.nextFloat() * 0.5f + 0.5f
            )
        }
    }

    val bubbleOffsets = bubbles.map { bubble ->
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (3000 + (bubble.speed * 20)).toInt(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "bubble"
        )
    }

    // Accelerometer for tilt
    val sensorManager = LocalContext.current.getSystemService(SensorManager::class.java)
    val lifecycleOwner = LocalLifecycleOwner.current

    var tiltX by remember { mutableStateOf(0f) }
    var tiltY by remember { mutableStateOf(0f) }

    val sensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    tiltX = -event.values[0] * 10
                    tiltY = event.values[1] * 10
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> sensorManager?.registerListener(
                    sensorListener, sensor, SensorManager.SENSOR_DELAY_GAME
                )
                Lifecycle.Event.ON_PAUSE -> sensorManager?.unregisterListener(sensorListener)
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            sensorManager?.unregisterListener(sensorListener)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            phase.snapTo(phase.value + speed)
            delay(16L)
        }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Draw water wave
        val path = Path().apply {
            moveTo(0f, height)
            for (x in 0..width.toInt() step 1) {
                val y = (amplitude * sin((2 * PI / wavelength) * (x + phase.value))).toFloat()
                lineTo(x.toFloat() + tiltX, height / 2 + y + tiltY)
            }
            lineTo(width, height)
            close()
        }

        drawPath(path = path, color = waveColor)

        // Draw bubbles
        bubbles.zip(bubbleOffsets).forEach { (bubble, anim) ->
            val x = bubble.x * width
            val y = height * anim.value
            drawCircle(
                color = bubbleColor.copy(alpha = bubble.alpha),
                radius = bubble.size,
                center = Offset(x, y)
            )
        }
    }
}

// Bubble data class
data class Bubble(
    val x: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)
