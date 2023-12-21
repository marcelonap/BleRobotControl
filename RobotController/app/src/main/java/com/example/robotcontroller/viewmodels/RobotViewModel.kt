package com.example.robotcontroller.viewmodels

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robotcontroller.data.ConnectionState
import com.example.robotcontroller.data.ReceiveManager
import com.example.robotcontroller.data.ScannedDevice
import com.example.robotcontroller.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class RobotViewModel @Inject constructor(
    private val receiveManager: ReceiveManager
): ViewModel() {

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)

    var connectedDevice by mutableStateOf<BluetoothDevice?>(null)

    var deviceListState = mutableStateListOf<BluetoothDevice>()
        private set
    var isDeviceConnected by mutableStateOf<Boolean>(false)
    var _deviceListState = mutableStateListOf<ScannedDevice>()
        private set
    var  terminalMessages = mutableStateListOf<String>()
        private set
    var  initializingMessage by mutableStateOf<String?>(null)
        private set
    var  errorMessage by mutableStateOf<String?>(null)
        private set

    private fun subscribeToChanges(){
        viewModelScope.launch {
            receiveManager.data.collect{result ->
                when(result){
                    is Resource.Success ->{
                        terminalMessages.add(result.data.message)
                    }

                    is Resource.Loading -> {
                        initializingMessage = result.message
                        connectionState = ConnectionState.CurrentlyInitializing

                    }

                    is Resource.Error ->{
                        errorMessage = result.errorMessage
                        connectionState = ConnectionState.Uninitialized
                    }

                    else -> {}
                }
            }
        }
        viewModelScope.launch {
            receiveManager.scannedDevice.collect{device->
                if(!deviceListState.contains(device)) {
                    deviceListState.add(device)
                    _deviceListState.add(parseBluetoothDevice(device))
                }
            }
        }
        viewModelScope.launch {
            receiveManager.connectedDevice.collect{device->
                connectedDevice = device
                connectionState = ConnectionState.Connected
                isDeviceConnected = true
            }
        }
    }

    fun parseBluetoothDevice(bluetoothDevice: BluetoothDevice): ScannedDevice {
        return ScannedDevice(
            //deviceId = bluetoothDevice.uuids?.toString()?.toLong(),
            deviceName = bluetoothDevice.name,
            address = bluetoothDevice.address
        )
    }

    fun reconnect() {
        receiveManager.reconnect()
    }
    fun startScan(){
        errorMessage = null
        subscribeToChanges()
        receiveManager.startScan()
    }
    fun disconnect(){
        receiveManager.disconnect()
        connectedDevice = null
        connectionState = ConnectionState.Disconnected
        receiveManager.closeConnection()
    }



}