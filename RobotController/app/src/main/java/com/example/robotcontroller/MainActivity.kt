package com.example.robotcontroller

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.robotcontroller.data.ConnectionState
import com.example.robotcontroller.presentation.Navigation
import com.example.robotcontroller.presentation.components.BluetoothPermissionTextProvider
import com.example.robotcontroller.presentation.components.PermissionDialog
import com.example.robotcontroller.presentation.permissions.BluetoothStateViewModel
import com.example.robotcontroller.presentation.permissions.PermissionUtils
import com.example.robotcontroller.ui.theme.RobotControllerTheme
import com.example.robotcontroller.viewmodels.PermissionsViewModel
import com.example.robotcontroller.viewmodels.RobotViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //Retrieving bluetoothAdapter
    @Inject
    lateinit var bluetoothAdapter: BluetoothAdapter

    private val viewModel: BluetoothStateViewModel by  viewModels()

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                // Handle Bluetooth state change
                onBluetoothStateChanged()
            }
        }
    }


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RobotControllerTheme {
                val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
                var deniedCount by remember { mutableStateOf(0) }
                var showRationaleDialog by remember { mutableStateOf(!permissionState.allPermissionsGranted) }
                val permissionTextProvider = BluetoothPermissionTextProvider()

                if (showRationaleDialog && !permissionState.allPermissionsGranted){
                    PermissionDialog(
                        permissionTextProvider = permissionTextProvider,
                        isPermanentlyDeclined = deniedCount >= 2 , // Set to true if permissions are permanently declined
                        onDismiss = {
                            showRationaleDialog = true
                        },
                        onOkClick = {
                            showRationaleDialog = false
                            permissionState.launchMultiplePermissionRequest()
                        },
                        onGoToAppSettingsClick = ::openAppSettings
                    )
                } else {
                    LaunchedEffect(permissionState) {
                        if (permissionState.allPermissionsGranted) {
                            deniedCount = 0 //Reset denied count
                            val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                            registerReceiver(bluetoothStateReceiver, filter)
                            bluetoothStateReceiver.onReceive(
                                context = this@MainActivity,
                                intent = Intent(BluetoothAdapter.ACTION_STATE_CHANGED)
                            )
                            showRationaleDialog = false
                        } else {
                            Log.d("PermissionTest", "$deniedCount")
                            deniedCount++
                            showRationaleDialog = true

                        }
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        onBluetoothStateChanged = {
                            onBluetoothStateChanged()
                        },
                        onButtonClicked = {
                            showBluetoothDialog()
                        }
                    )
                }
            }
        }
    }


    //Checking for bluetooth state and enabling when  onStart executes
    override fun onStart() {
        super.onStart()


    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(bluetoothStateReceiver)
    }

    private fun onBluetoothStateChanged() {
        val isBluetoothEnabled = bluetoothAdapter.isEnabled
        viewModel.setBluetoothEnabled(isBluetoothEnabled)
        showBluetoothDialog()
    }

    var isBluetoothDialogAlreadyShown = false //Flag to manage bluetooth dialog
    private fun showBluetoothDialog(){
        if(!bluetoothAdapter.isEnabled){ //checking if bluetooth is enabled
            if(!isBluetoothDialogAlreadyShown) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBluetoothIntentForResult.launch(enableBluetoothIntent)
                isBluetoothDialogAlreadyShown = true
            }
        }
    }

    //Requesting user to enable bluetooth
    private var startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            isBluetoothDialogAlreadyShown = false
            if(result.resultCode != Activity.RESULT_OK){
                showBluetoothDialog()
            }
        }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}