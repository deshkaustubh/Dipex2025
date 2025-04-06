package com.example.smartirrigationonlyread
//
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaterWave(
    modifier: Modifier = Modifier,
    waveColor: Color = Color(0xFF81D4FA),
    baseAmplitude: Float = 50f,
    wavelength: Float = 300f,
    speed: Float = 1.2f
) {
    val phase = remember { Animatable(0f) }
    val sensorManager = LocalContext.current.getSystemService(SensorManager::class.java)
    val lifecycleOwner = LocalLifecycleOwner.current

    var canvasHeight by remember { mutableStateOf(0f) }
    var rawX by remember { mutableStateOf(0f) }
    var rawZ by remember { mutableStateOf(0f) }

    val tiltOffsetX = remember { Animatable(0f) }
    val tiltOffsetY = remember { Animatable(0f) }
    val splashAmplitude = remember { Animatable(baseAmplitude) }

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    rawX = event.values[0]
                    rawZ = event.values[2]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    // Register sensor lifecycle
    DisposableEffect(Unit) {
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> sensorManager?.registerListener(
                    sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME
                )
                Lifecycle.Event.ON_PAUSE -> sensorManager?.unregisterListener(sensorEventListener)
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            sensorManager?.unregisterListener(sensorEventListener)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Animate tilt and splash constantly
    LaunchedEffect(rawX, rawZ, canvasHeight) {
        val isUpsideDown = rawZ < -7f

        // Animate the splash amplitude between two extremes rapidly
        launch {
            while (true) {
                splashAmplitude.animateTo(120f, tween(300, easing = FastOutLinearInEasing))
                splashAmplitude.animateTo(80f, tween(300, easing = LinearOutSlowInEasing))
            }
        }

        // Animate tilt offsets
        launch {
            tiltOffsetX.animateTo((-rawX * 15f).coerceIn(-150f, 150f), tween(400))
        }

        launch {
            tiltOffsetY.animateTo(if (isUpsideDown) canvasHeight / 2 else 0f, tween(1000))
        }
    }

    // Move wave forward continuously
    LaunchedEffect(Unit) {
        while (true) {
            phase.snapTo(phase.value + speed)
            kotlinx.coroutines.delay(16L)
        }
    }

    // Draw waves
    Canvas(modifier = modifier.onSizeChanged {
        canvasHeight = it.height.toFloat()
    }) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, height)
            for (x in 0..width.toInt()) {
                val y = (splashAmplitude.value * sin((2 * PI / wavelength) * (x + phase.value))).toFloat()
                lineTo(x + tiltOffsetX.value, height / 2 + y + tiltOffsetY.value)
            }
            lineTo(width, height)
            close()
        }

        drawIntoCanvas {
            drawPath(path = path, color = waveColor)
        }
    }
}
