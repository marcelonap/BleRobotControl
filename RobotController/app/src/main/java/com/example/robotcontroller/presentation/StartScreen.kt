package com.example.robotcontroller.presentation

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.robotcontroller.Greeting
import com.example.robotcontroller.ui.theme.RobotControllerTheme
import com.example.robotcontroller.viewmodels.RobotViewModel

@Composable
fun StartScreen(
    navController: NavController,
    viewModel: RobotViewModel = hiltViewModel(),
    onButtonClicked: () -> Unit
) {
    var sliderValue by remember { mutableStateOf(1f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Arrow Buttons with Icons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* Handle left arrow click */ }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Left")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { /* Handle up arrow click */ }) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Up")
                }
                IconButton(onClick = { /* Handle down arrow click */ }) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = "Down")
                }
            }
            IconButton(onClick = { /* Handle right arrow click */ }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Right")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Slider
        Text("Adjust Value: ${sliderValue.toInt()}")
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 1f..10f,
            steps = 8
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scan Devices Button
        Button(onClick = { /* Handle Scan Devices click */ }) {
            Text("Scan Devices")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    RobotControllerTheme {
       StartScreen(navController = rememberNavController()){

       }
    }
}
