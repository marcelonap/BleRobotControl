package com.example.robotcontroller.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.util.Log
import com.example.robotcontroller.data.ConnectionState
import com.example.robotcontroller.data.ReceiveManager
import com.example.robotcontroller.data.TempMessage
import com.example.robotcontroller.utils.Resource
import kotlinx.coroutines.launch
import java.util.UUID
@SuppressLint("MissingPermission")
class MyGattCallback(private val bleManager: BleReceiveManager) : BluetoothGattCallback() {
    var validateConnectionCounter = 0
    //Triggers on ANY connection state change (new connection, successful disconnection as well)
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) { //GATT_SUCCESS = A GATT operation completed successfully
            if (newState == BluetoothProfile.STATE_CONNECTED) { //Checking if newest connection state is connected
                bleManager.coroutineScope.launch {
                    //exposing state to rest of app

                    bleManager.data.emit(Resource.Loading(message = "Discovering Services..."))
                }
                //Attempting to discover services
                //discoverServices : Discovers services offered by a remote device as well as their characteristics and descriptors.
                //triggers onServicesDiscovered callback @103
                gatt.discoverServices()
                bleManager.gatt = gatt // Updates gatt instance
                bleManager.coroutineScope.launch {
                    bleManager.connectedDevice.emit(gatt.device)
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//Checks for connection state Disconnected
                bleManager.coroutineScope.launch {
                    //exposing state to rest of the app
                    bleManager.data.emit(Resource.Success(data = TempMessage(ConnectionState.Disconnected , message = "failed to connect")))
                }
                //closing gatt client
                gatt.close()
            }
        } else if(status != 1 ){ //if status was not GATT_SUCCESS
            gatt.close()
            bleManager.currentConnectionAttempt += 1 //Increment attempt counter
            bleManager.coroutineScope.launch {
                //expose attempt state to rest of the app
                bleManager.data.emit(Resource.Loading(message = "Attempting to connect $bleManager.currentConnectionAttempt/$bleManager.MAXIMUM_CONNECTION_ATTEMPTS "))
                Log.d("DeviceFound", "state when failing : $newState")
            }

            if (bleManager.currentConnectionAttempt <= bleManager.MAXIMUM_CONNECTION_ATTEMPTS) {
                //If within attempt limit, retry connection
                // startReceiving()
                bleManager.connect(gatt.device)
            } else {
                bleManager.coroutineScope.launch {
                    //exposing state to rest of app
                    bleManager.data.emit(Resource.Error(errorMessage = "Unable to pair with device"))
                }
            }
        }
    }

    //triggers after discoverServices()
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        with(gatt) {
            Log.d("DeviceFound" ," Attempting to print gatt table")
            printGattTable()
            bleManager.coroutineScope.launch {
                bleManager.data.emit(Resource.Loading(message = "Adjusting MTU space..."))
            }
            bleManager.gatt = gatt
            gatt.requestMtu(517) //Maximum data amount that can be sent from ble to phone device ranges from [20-517]
        }
    }

    override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
        Log.d("DeviceFound", "mtu received: $mtu")

        val robotCharacteristic= bleManager.findCharacteristic(BLEGattAttributes.RobotService.uuid, BLEGattAttributes.RobotCharacteristic.uuid)
        if (robotCharacteristic == null) { //Checking on stored characteristic
            bleManager.coroutineScope.launch {
                bleManager.data.emit(Resource.Error(errorMessage = "Could not find robot"))
            }
            return
        }
        bleManager.coroutineScope.launch {
            bleManager.connectedDevice.emit(gatt.device)
        }

        validateConnectionCounter = 0
        Log.d("DeviceFound" ," Attempting to write and receive")

    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int,
        value: ByteArray
    ) {
        //super.onDescriptorRead(gatt, descriptor, status, value)
        Log.d("DeviceFound", value.toString())
    }
    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        status: Int,

        ) {

        with(characteristic) {

            when (uuid.toString()) {
                BLEGattAttributes.TUART_TX.uuid -> {

                    val msg = value.toString()
                    Log.d("DeviceFound", msg)
                    val tempMessage = TempMessage(connectionState = ConnectionState.Connected, message = msg)
                    bleManager.coroutineScope.launch {
                        bleManager.data.emit(Resource.Success(data=tempMessage))
                    }
                }
                else -> Unit
            }
        }

    }

    /*TODO*/
    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        if(status == BluetoothGatt.GATT_SUCCESS){
            bleManager.coroutineScope.launch {
                bleManager.data.emit(Resource.Success(TempMessage(connectionState = ConnectionState.Connected,message = "Successful write" )))
            }
        }else{
            bleManager.coroutineScope.launch {
                bleManager.data.emit(Resource.Error("somethign went wrong when writing!"))
            }
        }
        super.onCharacteristicWrite(gatt, characteristic, status)
        bleManager.canWrite = true
    }

}