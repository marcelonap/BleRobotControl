package com.example.robotcontroller.presentation.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.support.v4.os.IResultReceiver.Default
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.robotcontroller.viewmodels.RobotViewModel
import com.example.robotcontroller.data.ScannedDevice
import com.example.robotcontroller.ui.theme.RobotControllerTheme

@SuppressLint("MissingPermission")
@Composable
fun DeviceDisplay(
    bluetoothDevice: BluetoothDevice?,
    modifier : Modifier = Modifier,
    onClick: (BluetoothDevice) -> Unit,
    scannedDevice: ScannedDevice
){
    /*
    TODO
        Display found device info and expose clicked device to
        ble receive manager to initiate connection

     */
    Box(modifier = modifier
       // .border(shape = MaterialTheme.shapes.large, width = .5f.dp, color = DefaultShadowColor)
        .background(color = MaterialTheme.colorScheme.tertiaryContainer, shape = MaterialTheme.shapes.small)

    ){
        Row(
            modifier = Modifier
                .wrapContentWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(2f)
            ) {
                if (!scannedDevice.deviceName.isNullOrBlank()) {
                    Text(text = scannedDevice.deviceName)
                }

            }
            Button(onClick = {  onClick(bluetoothDevice!!) },
                modifier = modifier
                    .padding(top = 2.dp, end = 8.dp)
                    .weight(1f),
            shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors()){
                Text(text = "Connect")
            }
        }
        
    }
}

@Composable
fun DeviceList(
    deviceList : List<BluetoothDevice>,
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
){
    LazyColumn(modifier = modifier.padding(top = 6.dp)) {
        //val _deviceList: List<BluetoothDevice> = deviceList!!.toList()
        items(deviceList) { device ->
            DeviceDisplay(
                bluetoothDevice = device,
                onClick = { viewModel.connect(device) },
                scannedDevice = viewModel.parseBluetoothDevice(device)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}


@Preview(showBackground = false)
@Composable
fun DeviceDisplayPreview(){
    RobotControllerTheme{
        DeviceDisplay( bluetoothDevice = null, onClick = {}, scannedDevice = ScannedDevice(
            //    deviceId = 122134234,
            deviceName = "marcelo",
            address = "67890"
        )
        )
    }

}

