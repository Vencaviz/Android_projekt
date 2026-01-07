package com.projekt.xvizvary.ui.screens.tools

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
fun InterestRateScreen() {
    val rows = listOf(
        "1m - 4.50%",
        "2m - 5.50%",
        "6m - 2.50%",
        "12m - 5.80%"
    )

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = stringResource(R.string.screen_interest_rate), style = MaterialTheme.typography.titleLarge)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rows) { row ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(text = row, modifier = Modifier.padding(14.dp))
                }
            }
        }
    }
}

