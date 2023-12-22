package com.example.robotcontroller.presentation






import android.util.Log
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.example.robotcontroller.data.ConnectionState
import com.example.robotcontroller.presentation.permissions.BluetoothStateViewModel
import com.example.robotcontroller.presentation.permissions.PermissionUtils
import com.example.robotcontroller.viewmodels.RobotViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Navigation(
    onBluetoothStateChanged: () -> Unit,
    onButtonClicked: () -> Unit,
    bluetoothViewModel : BluetoothStateViewModel = hiltViewModel(),
    viewModel: RobotViewModel = hiltViewModel(),
){
    //Setting up navController to manage navigation between pages
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
    var showPermissionDialog by remember { mutableStateOf(false) }
    val bleConnectionState = viewModel.connectionState




    //Instantiating navHost with our navcontroller and our starting screen
    NavHost(navController = navController, startDestination = Screen.StartScreen.route ){

        composable(Screen.StartScreen.route){
            StartScreen(
                navController = navController,
                onButtonClicked = onButtonClicked,
                onBluetoothStateChanged = onBluetoothStateChanged)
        }
    }
}

sealed class Screen(val route: String){
    object StartScreen : Screen("start_screen")

}