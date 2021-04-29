package com.danegor.podlodkahw.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ExitDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        text = {
            Text(
                "Вы уверены, что хотите выйти из приложения?",
                style = MaterialTheme.typography.body1
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    "ДА",
                    style = MaterialTheme.typography.button
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "ОТМЕНА",
                    style = MaterialTheme.typography.button
                )
            }
        }
    )
}
