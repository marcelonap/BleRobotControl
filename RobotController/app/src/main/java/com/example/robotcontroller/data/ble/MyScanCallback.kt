package com.example.robotcontroller.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import com.example.robotcontroller.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@SuppressLint("MissingPermission")
class MyScanCallback(private val bleManager: BleReceiveManager) : ScanCallback() {

    var count = 1
    var listOfDevices = ArrayList<BluetoothDevice>()
    //onScanResult triggers when scanner detects a BLE device within range

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        //Add compatible devices to list and expose list of devices to rest of the app
        //in order to display on user screen

        //Checking if detected device matches name

        if(bleManager.isScanning && !listOfDevices.contains(result.device) && !result.device.name.isNullOrBlank() ){
            // if(result.device.name.contains("")){
            if ( !result.device.name.isNullOrBlank()) {
                listOfDevices.add(result.device)
                bleManager.coroutineScope.launch {
                    bleManager.data.emit(Resource.Loading(message = "Finding Devices..."))
                    bleManager.scannedDevice.emit(result.device)
                    delay(10000)
                }
                Log.d("DeviceFound", " name: ${result.scanRecord?.deviceName}")
                Log.d("DeviceFound", " uuid: ${result.scanRecord?.serviceUuids.toString()}")
                Log.d("DeviceFound", " found ${count++} devices")
            }
        }

    }
    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
    }
}