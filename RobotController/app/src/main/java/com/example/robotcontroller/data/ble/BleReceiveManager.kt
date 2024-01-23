package com.example.robotcontroller.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.util.Log
import com.example.robotcontroller.data.ReceiveManager
import com.example.robotcontroller.data.TempMessage
import com.example.robotcontroller.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
sealed class BLEGattAttributes(val uuid:String){
    object MicroTransparentUART : BLEGattAttributes ("49535343-FE7D-4AE5-8FA9-9FAFD205E455")
    object TUART_TX : BLEGattAttributes("49535343-1e4d-4bd9-ba61-23c647249616")
    object TUART_RX : BLEGattAttributes("49535343-8841-43f4-a8d4-ecbe34729bb3")
    object RobotCharacteristic : BLEGattAttributes("D973f2E2-B19E-11E2-9E96-0800200C9A66")
    object RobotService : BLEGattAttributes("D973f2E0-B19E-11E2-9E96-0800200C9A66")
}
@SuppressLint("MissingPermission")
class BleReceiveManager @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) : ReceiveManager {

    val MAXIMUM_CONNECTION_ATTEMPTS = 5
    //Declaring our gatt for connection handling
    internal var gatt: BluetoothGatt? = null
    //Scanning flag
    internal var isScanning = false
    internal var canWrite = true
    //Defining coroutine scope
    internal val coroutineScope = CoroutineScope(Dispatchers.Default)

    var currentConnectionAttempt = 1
    //-------------Flows---------------------
    override val data: MutableSharedFlow<Resource<TempMessage>> = MutableSharedFlow()
    //Flow for each new scanned device
    override var scannedDevice: MutableSharedFlow<BluetoothDevice> = MutableSharedFlow()
    //Flow for device that was connected to
    override val connectedDevice: MutableSharedFlow<BluetoothDevice> = MutableSharedFlow()



    //Define BLE scanner, lazy = initialized if required
    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val myScanCallback = MyScanCallback(this)

    private val myGattCallback = MyGattCallback(this)
    override fun reconnect(){
        gatt?.connect()
    }
    override fun disconnect(){
        gatt?.disconnect()
    }
    override fun startScan(){
        coroutineScope.launch{
            //Exposing state to rest of the app
            data.emit(Resource.Loading(message = "Looking for robot..."))
        }
        isScanning = true //setting scan flag
        //Starting BLE scan: no filters, scanSettings object created @49, scanCallBack @171
        Log.d("DeviceFound"," startScan called")
        bleScanner.startScan(myScanCallback)
    }
    override fun closeConnection()
    {
        bleScanner.stopScan(myScanCallback)
        val characteristic= findCharacteristic(BLEGattAttributes.MicroTransparentUART.uuid, BLEGattAttributes.TUART_TX.uuid)
        if(characteristic != null){
            disconnectCharacteristic(characteristic)
        }
        gatt?.close()
    }
    override fun connect(device: BluetoothDevice)
    {
        isScanning = false //Updating scan state boolean
        bleScanner.stopScan(myScanCallback)
        coroutineScope.launch {
            data.emit(Resource.Loading(message = "Connecting..."))
        }

        device.connectGatt(
            context,
            false,
            myGattCallback, //GattCallBack @67
            BluetoothDevice.TRANSPORT_LE
        )
    }
    override fun stopScan()
    {
        coroutineScope.launch {
            data.emit(Resource.Error(errorMessage = "Scan stopped"))
        }
        bleScanner.stopScan(myScanCallback)
    }
    override fun writeMove(move: String)
    {
        val robotCharacteristic =
            findCharacteristic(BLEGattAttributes.RobotService.uuid, BLEGattAttributes.RobotCharacteristic.uuid)
                ?: return

        writeCharacteristic(robotCharacteristic,move)

    }

    //Helper methods
    internal fun findCharacteristic(serviceUUID: String, characteristicUUID: String): BluetoothGattCharacteristic?{

        for (service in gatt!!.services) {
            service.characteristics.forEach{char ->
                if(char.uuid.toString().contains(characteristicUUID)){
                    return char
                }
            }
        }
        return null
    }

    private fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, payload: String) {

        val writeType = when {

            characteristic.isWritableWithoutResponse() -> {
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            }
            // characteristic.isWritable() -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            else -> error("Characteristic ${characteristic.uuid} cannot be written to")
        }

        gatt?.let { gatt ->
            characteristic.writeType = writeType
            characteristic.setValue(payload.toByteArray(Charsets.UTF_8))
            gatt.writeCharacteristic(characteristic)
        } ?: error("Not connected to a BLE device!")
    }
    private fun disconnectCharacteristic(characteristic: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString( CCCD_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid)?.let{cccdDescriptor->
            if(gatt?.setCharacteristicNotification(characteristic,false)== false){
                Log.d("ReceiveManager", "set characteristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }

    private fun writeDescription(descriptor: BluetoothGattDescriptor, payload: ByteArray){
        // Log.d("DeviceFound" ," Attempting to write description")

        gatt?.let{gatt ->
            descriptor.value = payload
            gatt.writeDescriptor(descriptor)

        }?: error("Not connected to a BLE device!")
        Log.d("DeviceFound" ," Attempting to write description")
    }



}
