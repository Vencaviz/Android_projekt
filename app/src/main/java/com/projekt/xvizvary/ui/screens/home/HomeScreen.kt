package com.projekt.xvizvary.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.projekt.xvizvary.R

@Composable
fun HomeScreen(
    onAddTransaction: () -> Unit
) {
    val transactions = listOf(
        "Car shop  -500 Kč",
        "Albert  -350 Kč",
        "Salary  +25 000 Kč"
    )

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(R.string.screen_overview), style = MaterialTheme.typography.titleLarge)
                Text(
                    text = stringResource(R.string.label_this_month, "5 000 Kč"),
                    color = MaterialTheme.colorScheme.primary
                )
                Button(onClick = onAddTransaction) { Text(text = stringResource(R.string.action_add_transaction)) }
            }
        }

        Text(text = stringResource(R.string.title_transactions), style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(transactions) { t ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = t,
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

