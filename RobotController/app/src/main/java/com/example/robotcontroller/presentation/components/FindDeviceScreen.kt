package com.example.robotcontroller.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.robotcontroller.data.ConnectionState
import com.example.robotcontroller.presentation.components.DeviceList
import com.example.robotcontroller.viewmodels.RobotViewModel

@Composable
fun FindDeviceScreen(
    innerPadding: PaddingValues,
    viewModel: RobotViewModel,

    ){
    var canAttemptScan by rememberSaveable{ mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.BottomCenter
    ){
        Row(modifier = Modifier
            .paddingFromBaseline(bottom = 0.dp)
        ) {
            Button(
                enabled = canAttemptScan,
                modifier = Modifier

                    //.wrapContentSize(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.medium,
                onClick = {
                    viewModel.startScan()
                    canAttemptScan = false
                }
            ) {
                Text(text = "Look for Robot")
            }

            Button(
                enabled = !canAttemptScan,
                modifier = Modifier

                    //   .wrapContentSize(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.medium,
                onClick = {
                    viewModel.stopScanning()
                    canAttemptScan = true
                }
            ) {
                Text(text = "Stop looking")
            }

        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {


            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .aspectRatio(1f)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewModel.connectionState == ConnectionState.CurrentlyInitializing ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    CircularProgressIndicator()
                    if (viewModel.initializingMessage != null) {
                        Text(text = viewModel.initializingMessage!!)
                    }
                }

                if (viewModel.connectionState == ConnectionState.Uninitialized) {
                    Text(text = "Devices Found:")
                }
                if (!viewModel.deviceListState.isEmpty()) {
                    DeviceList(
                        deviceList = viewModel.deviceListState,
                        viewModel = viewModel,
                        modifier = Modifier.padding(top = 100.dp)
                    )
                }
            }
        }
    }
}