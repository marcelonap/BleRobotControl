package com.example.robotcontroller.data.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import com.example.robotcontroller.utils.bits
import com.example.robotcontroller.utils.bitsToHex
import com.example.robotcontroller.utils.toHex
import java.util.*


//Characteristics : A characteristic contains a single value and 0-n descriptors that describe the characteristic's value.
// A characteristic can be thought of as a type, analogous to a class
// Characteristics have the following attributes:
//
//• Value: Data value storage for the characteristic
//• Permissions: Access (Read, Write, Notify, Indicate), Encryption, Authorization
//• Type: Unique addressable 16/32/128-bit UUID identifier
//• Handle: Unique addressable 16-bit identifier for each attribute.

//Descriptor: defined attributes that describe a characteristic value
// i.e:  human-readable description, an acceptable range for a characteristic's value,
// or a unit of measure that is specific to a characteristic's value.
//Service : collection of characteristics.


//Bridge needs to be enabled, If can't find machine type, ask user (default JD)

//Enable debug
//Find debug out (getIdRequests
//check for dDiagnostic tag
//$debug, 4068
//$getVersion

//UUID: unique identifier for attributes
const val CCCD_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb" // change to specific UUID descriptor for our sensor

fun getReadStringFromBytes(byteArray: ByteArray): String {
    val bitSet = byteArray.bits()
    val cat = bitSet.substring(0, 10).toInt(2).toHex()
    val subcat = bitSet.substring(10, bitSet.length).toInt(2).toHex()

    val sb = StringBuilder()
    with (sb) {
        appendLine("Bits, Categories, Value:")
        appendLine(bitSet)
        appendLine("$cat $subcat")
        appendLine(byteArray.bitsToHex())
    }

    return sb.toString()

}
fun BluetoothGatt.printGattTable() {
    if (services.isEmpty()) {
        Log.d("DeviceFound","No service and characteristic available, call discoverServices() first?")
        return
    }
    services.forEach { service ->
        val characteristicsTable = service.characteristics.joinToString(
            separator = "\n|--",
            prefix = "|--"
        ) { char ->
            var description = "${char.uuid}: ${char.printProperties()}"
            if (char.descriptors.isNotEmpty()) {
                description += "\n" + char.descriptors.joinToString(
                    separator = "\n|------",
                    prefix = "|------"
                ) { descriptor ->
                    "${descriptor.uuid}: ${descriptor.printProperties()}"
                }
            }
            description
        }
        Log.d("DeviceFound","Service ${service.uuid}\nCharacteristics:\n$characteristicsTable")
    }
}
fun BluetoothGattCharacteristic.printProperties(): String = mutableListOf<String>().apply {
    if (isReadable()) add("READABLE")
    if (isWritable()) add("WRITABLE")
    if (isWritableWithoutResponse()) add("WRITABLE WITHOUT RESPONSE")
    if (isIndicatable()) add("INDICATABLE")
    if (isNotifiable()) add("NOTIFIABLE")
    if (isEmpty()) add("EMPTY")
}.joinToString()

fun BluetoothGattCharacteristic.isReadable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

fun BluetoothGattCharacteristic.isWritable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

fun BluetoothGattCharacteristic.isIndicatable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE)

fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean =
    properties and property != 0

fun BluetoothGattDescriptor.printProperties(): String = mutableListOf<String>().apply {
    if (isReadable()) add("READABLE")
    if (isWritable()) add("WRITABLE")
    if (isEmpty()) add("EMPTY")
}.joinToString()

fun BluetoothGattDescriptor.isReadable(): Boolean =
    containsPermission(BluetoothGattDescriptor.PERMISSION_READ)

fun BluetoothGattDescriptor.isWritable(): Boolean =
    containsPermission(BluetoothGattDescriptor.PERMISSION_WRITE)

fun BluetoothGattDescriptor.containsPermission(permission: Int): Boolean =
    permissions and permission != 0

fun BluetoothGattDescriptor.isCccd() =
    uuid.toString().uppercase(Locale.US) == CCCD_DESCRIPTOR_UUID.uppercase(Locale.US)

fun ByteArray.toHexString(): String =
    joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }