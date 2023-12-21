package com.example.robotcontroller.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.robotcontroller.data.ble.BleReceiveManager
import com.example.robotcontroller.data.ReceiveManager
import com.example.robotcontroller.presentation.permissions.BluetoothStateViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //test
    //getting bluetooth adapter and providing it to application
    //obtain an instance of the BluetoothManager system service. Calling BluetoothManager#getAdapter will give you a BluetoothAdapter object
    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }



    @Provides
    @Singleton
    fun provideReceiveManager(
        @ApplicationContext context : Context,
        bluetoothAdapter: BluetoothAdapter
    ) : ReceiveManager {
        return BleReceiveManager(bluetoothAdapter,context)
    }
}