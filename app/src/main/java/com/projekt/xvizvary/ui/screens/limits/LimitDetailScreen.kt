package com.projekt.xvizvary.ui.screens.limits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.projekt.xvizvary.R

@Composable
fun LimitDetailScreen() {
    val (name, setName) = remember { mutableStateOf("") }
    val (date, setDate) = remember { mutableStateOf("") }
    val (amount, setAmount) = remember { mutableStateOf("") }
    val (category, setCategory) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.action_add_transaction))

        OutlinedTextField(
            value = name,
            onValueChange = setName,
            label = { Text(stringResource(R.string.label_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = date,
            onValueChange = setDate,
            label = { Text(stringResource(R.string.label_date)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = amount,
            onValueChange = setAmount,
            label = { Text(stringResource(R.string.label_amount)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = category,
            onValueChange = setCategory,
            label = { Text(stringResource(R.string.label_category)) },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { /* save later */ }, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.action_add_transaction))
        }
    }
}

