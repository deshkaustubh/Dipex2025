package com.example.smartirrigationonlyread

import androidx.compose.runtime.*
import com.google.firebase.database.*

@Composable
fun MotorStatusObserver(motorRef: DatabaseReference): State<Boolean> {

    val motorStatus = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        motorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                motorStatus.value = snapshot.getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    return motorStatus
}
