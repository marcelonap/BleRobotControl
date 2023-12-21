package com.example.robotcontroller

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.robotcontroller.presentation.Navigation
import com.example.robotcontroller.presentation.permissions.BluetoothStateViewModel
import com.example.robotcontroller.ui.theme.RobotControllerTheme
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RobotControllerTheme {
                // A surface container using the 'background' color from the theme
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
        showBluetoothDialog()
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver, filter)
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

