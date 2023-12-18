package com.example.robotcontroller.data

sealed interface ConnectionState
{
    object Connected : ConnectionState
    object Disconnected : ConnectionState
    object Uninitialized: ConnectionState
    object CurrentlyInitializing : ConnectionState

}
