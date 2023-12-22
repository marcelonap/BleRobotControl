package com.example.robotcontroller.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PermissionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onGrantPermissions: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Permissions Required") },
            text = { Text("This app requires certain permissions to function properly. Please grant them.") },
            confirmButton = {
                Button(onClick = onGrantPermissions) {
                    Text("Grant Permissions")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Not Now")
                }
            }
        )
    }
}
