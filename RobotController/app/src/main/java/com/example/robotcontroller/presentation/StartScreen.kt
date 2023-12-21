package com.example.robotcontroller.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.robotcontroller.data.ConnectionState
import com.example.robotcontroller.presentation.components.FindDeviceScreen
import com.example.robotcontroller.presentation.permissions.BluetoothStateViewModel
import com.example.robotcontroller.presentation.permissions.PermissionUtils
import com.example.robotcontroller.ui.theme.RobotControllerTheme
import com.example.robotcontroller.viewmodels.RobotViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartScreen(
    navController: NavController,
    viewModel: RobotViewModel = hiltViewModel(),
    bluetoothViewModel : BluetoothStateViewModel = hiltViewModel(),
    onButtonClicked: () -> Unit,
    onBluetoothStateChanged: ()-> Unit
) {
    var sliderValue by remember { mutableStateOf(1f) }
    //collecting bluetooth adapter state globally
    val isBluetoothEnabled by bluetoothViewModel.bluetoothEnabled.collectAsState()

    var canAttemptScan by rememberSaveable{ mutableStateOf(false)}
    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{_,event ->
                if (event == Lifecycle.Event.ON_START) {
                    permissionState.launchMultiplePermissionRequest()
                    if (permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected) {
                        viewModel.reconnect()
                    }
                }
                if (event == Lifecycle.Event.ON_STOP) {
                    if (bleConnectionState == ConnectionState.Connected) {
                        viewModel.disconnect()
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    LaunchedEffect(
        key1 = permissionState.allPermissionsGranted,
    ){
        if(permissionState.allPermissionsGranted){
            if(bleConnectionState == ConnectionState.Uninitialized){
                canAttemptScan = true
            }
        }
    }

    LaunchedEffect(
        key1 = isBluetoothEnabled,
    ){
        if(!isBluetoothEnabled){
            onBluetoothStateChanged()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Arrow Buttons with Icons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(enabled = viewModel.isDeviceConnected, onClick = { viewModel.writeMove("@ML0") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Left")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(enabled = viewModel.isDeviceConnected, onClick = { viewModel.writeMove("@MF0")}) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Forward")
                }
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(enabled = viewModel.isDeviceConnected, onClick = { viewModel.writeMove("@MB0") }) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = "Backward")
                }
            }
            IconButton(enabled = viewModel.isDeviceConnected,onClick = { viewModel.writeMove("@MR0")}) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Right")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Slider
        Text("Adjust Value: ${sliderValue.toInt()}")
        Slider(
            enabled = viewModel.isDeviceConnected,
            value = sliderValue,
            onValueChange = { sliderValue = it
                viewModel.writeMove("@S$sliderValue")},
            valueRange = 0f..10f,
            steps = 0
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scan Devices Button
//        Button(onClick = { viewModel.startScan() }) {
//            Text("Scan Devices")
//        }
        if(!viewModel.isDeviceConnected && canAttemptScan) {
            FindDeviceScreen(innerPadding = PaddingValues(8.dp), viewModel = viewModel)
        }else if(viewModel.isDeviceConnected){
            Text("Connected to ${viewModel.connectedDevice!!.name}")
        }
        }
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    RobotControllerTheme {
       StartScreen(navController = rememberNavController(), onButtonClicked = {}, onBluetoothStateChanged = {})
    }
}
