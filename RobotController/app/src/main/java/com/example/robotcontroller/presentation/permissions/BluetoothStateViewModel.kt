package com.example.robotcontroller.presentation.permissions

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BluetoothStateViewModel @Inject constructor() : ViewModel() {
    private val _bluetoothEnabled = MutableStateFlow(false) // Default value
    val bluetoothEnabled: StateFlow<Boolean> = _bluetoothEnabled

    fun setBluetoothEnabled(enabled: Boolean) {
        _bluetoothEnabled.value = enabled
    }
}