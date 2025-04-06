package com.example.smartirrigationonlyread

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartirrigationonlyread.ui.theme.SmartIrrigationOnlyReadTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.database.*

data class SectorData(
    val avgMoisture: Float = 0f,
    val valveStatus: Boolean = false,
    val working: Int = 0,
    val sensor1Ack: Boolean = false,
    val sensor2Ack: Boolean = false
)

class MainActivity : ComponentActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var motorRef: DatabaseReference
    private lateinit var sectorsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance()
        motorRef = database.reference.child("motor").child("status")
        sectorsRef = database.reference.child("sectors")

        setContent {
            SmartIrrigationOnlyReadTheme {
                var motorStatus by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFB2DFDB),
                                    Color(0xFFE0F7FA)
                                )
                            )
                        )
                ) {
                    if (motorStatus) {
                        WaterWaveWithBubbles(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),
                            waveColor = Color(0xFF00BCD4),
                            bubbleColor = Color.White
                        )
                    } else {
                        WaterWave(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),
                            waveColor = Color(0xFF81D4FA)
                        )
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Transparent
                    ) {
                        SectorDashboard(
                            sectorsRef = sectorsRef,
                            motorRef = motorRef,
                            onMotorStatusChanged = { status ->
                                motorStatus = status
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectorDashboard(
    sectorsRef: DatabaseReference,
    motorRef: DatabaseReference,
    onMotorStatusChanged: (Boolean) -> Unit
) {
    var sector1Data by remember { mutableStateOf(SectorData()) }
    var sector2Data by remember { mutableStateOf(SectorData()) }
    var motorStatus by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun checkConnectivity(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun fetchData() {
        if (!checkConnectivity()) return
        isRefreshing = true

        sectorsRef.child("sector1").get().addOnSuccessListener {
            sector1Data = it.getValue(SectorData::class.java) ?: SectorData()
        }

        sectorsRef.child("sector2").get().addOnSuccessListener {
            sector2Data = it.getValue(SectorData::class.java) ?: SectorData()
        }

        motorRef.get().addOnSuccessListener {
            val status = it.getValue(Boolean::class.java) ?: false
            motorStatus = status
            onMotorStatusChanged(status)
        }.addOnCompleteListener {
            isRefreshing = false
        }
    }

    LaunchedEffect(true) {
        sectorsRef.child("sector1").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sector1Data = snapshot.getValue(SectorData::class.java) ?: SectorData()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        sectorsRef.child("sector2").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sector2Data = snapshot.getValue(SectorData::class.java) ?: SectorData()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        motorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(Boolean::class.java) ?: false
                motorStatus = status
                onMotorStatusChanged(status)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { fetchData() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Smart Irrigation Monitor 🌿",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            MotorStatusCard(status = motorStatus)
            Spacer(modifier = Modifier.height(16.dp))
            SectorCard(title = "Sector 1", data = sector1Data, color = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(16.dp))
            SectorCard(title = "Sector 2", data = sector2Data, color = Color(0xFF2196F3))
        }
    }
}

@Composable
fun SectorCard(
    title: String,
    data: SectorData,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedMoisture by animateFloatAsState(
        targetValue = data.avgMoisture.coerceIn(0f, 100f),
        label = "MoistureAnim"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .width(30.dp)
                        .background(color = MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(5.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(5.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(animatedMoisture / 100f)
                            .background(color, RoundedCornerShape(5.dp))
                            .align(Alignment.BottomCenter)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column {
                    Text("Moisture: ${data.avgMoisture.toInt()}%", fontSize = 16.sp)
                    Text("Valve: ${if (data.valveStatus) "On" else "Off"}", fontSize = 16.sp)
                    Text("Working: ${if (data.working == 1) "Yes" else "No"}", fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sensor 1: ", fontSize = 16.sp)
                        Icon(
                            imageVector = if (data.sensor1Ack) Icons.Default.Done else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (data.sensor1Ack) Color(0xFF4CAF50) else Color.Red
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Sensor 2: ", fontSize = 16.sp)
                        Icon(
                            imageVector = if (data.sensor2Ack) Icons.Default.Done else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (data.sensor2Ack) Color(0xFF4CAF50) else Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MotorStatusCard(status: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Motor Status: ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                if (status) "Running ✅" else "Stopped ❌",
                fontSize = 18.sp,
                color = if (status) Color(0xFF4CAF50) else Color.Red
            )
        }
    }
}
