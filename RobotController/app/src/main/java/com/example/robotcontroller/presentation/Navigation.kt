package com.example.robotcontroller.presentation






import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(
    onBluetoothStateChanged: () -> Unit,
    onButtonClicked: () -> Unit
){
    //Setting up navController to manage navigation between pages
    val navController = rememberNavController()

    //Instantiating navHost with our navcontroller and our starting screen
    NavHost(navController = navController, startDestination = Screen.StartScreen.route ){
        composable(Screen.StartScreen.route){
            StartScreen(
                navController = navController,
                onButtonClicked = onButtonClicked)
        }
    }
}

sealed class Screen(val route: String){
    object StartScreen : Screen("start_screen")

}