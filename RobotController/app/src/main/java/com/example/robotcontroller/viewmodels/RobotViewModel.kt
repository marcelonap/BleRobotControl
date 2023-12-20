package com.example.robotcontroller.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.example.robotcontroller.data.ReceiveManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class RobotViewModel @Inject constructor(
    private val receiveManager: ReceiveManager
): ViewModel() {



}