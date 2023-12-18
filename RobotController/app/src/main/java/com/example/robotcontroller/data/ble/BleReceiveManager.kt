package com.example.robotcontroller.data.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.example.robotcontroller.data.ReceiveManager
import com.example.robotcontroller.data.TempMessage
import com.example.robotcontroller.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class BleReceiveManager @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    context: Context
) : ReceiveManager {
    //Defining coroutine scope
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    //Declaring our gatt for connection handling
    private var gatt: BluetoothGatt? = null

    override val data: MutableSharedFlow<Resource<TempMessage>> = MutableSharedFlow()
    override val scannedDevice: MutableSharedFlow<BluetoothDevice> = MutableSharedFlow()
    override val connectedDevice: MutableSharedFlow<BluetoothDevice> = MutableSharedFlow()

    override fun reconnect(){

    }
    override fun disconnect(){

    }
    override fun startReceiving(){

    }
    override fun closeConnection(){}
    override fun connect(device: BluetoothDevice) {}
    override fun stopScan(){}
    override fun writeToChar(characteristic: BluetoothGattCharacteristic){}


}
