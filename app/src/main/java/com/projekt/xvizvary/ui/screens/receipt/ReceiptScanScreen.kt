package com.projekt.xvizvary.ui.screens.receipt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.projekt.xvizvary.R

@Composable
fun ReceiptScanScreen() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.action_scan_receipt), style = MaterialTheme.typography.titleLarge)
        Text(text = stringResource(R.string.message_empty))
        Button(onClick = { /* Camera flow will be added with ML Kit */ }, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.action_scan_receipt))
        }
    }
}

