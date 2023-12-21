package com.example.robotcontroller.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

@SuppressLint("MissingPermission")
data class ScannedDevice(
   // val deviceId: Long? = null,
    val deviceName: String?,
    val address: String,
 //   val services: List<String>? = null,
    // val lastSeen: Long

)
