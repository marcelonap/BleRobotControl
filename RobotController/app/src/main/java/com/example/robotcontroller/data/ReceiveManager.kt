package com.example.robotcontroller.data

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import com.example.robotcontroller.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface ReceiveManager {
    val data: MutableSharedFlow<Resource<TempMessage>>
    val scannedDevice: MutableSharedFlow<BluetoothDevice>
    val connectedDevice: MutableSharedFlow<BluetoothDevice>

    fun reconnect()
    fun disconnect()
    fun startScan()
    fun closeConnection()
    fun connect(device: BluetoothDevice)
    fun stopScan()
    fun writeMove(move:String)

}