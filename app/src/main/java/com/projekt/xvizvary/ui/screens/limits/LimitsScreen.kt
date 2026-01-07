package com.projekt.xvizvary.ui.screens.limits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.projekt.xvizvary.R

@Composable
fun LimitsScreen(
    onLimitClick: () -> Unit
) {
    val limits = listOf(
        LimitRow("Car", "2 500 Kč / 1 150 Kč"),
        LimitRow("Food", "5 000 Kč / 2 370 Kč"),
        LimitRow("Fun", "3 000 Kč / 1 420 Kč")
    )

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = stringResource(R.string.screen_limits), style = MaterialTheme.typography.titleLarge)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(limits) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onLimitClick
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = item.category, style = MaterialTheme.typography.titleMedium)
                        Text(text = item.progress, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

private data class LimitRow(
    val category: String,
    val progress: String
)

