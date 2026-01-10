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
fun ExchangeRateScreen() {
    val rows = listOf(
        RowData("Vietnam", "1.403", "1.746"),
        RowData("Korea", "3.704", "5.151"),
        RowData("China", "1.725", "2.234")
    )

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = stringResource(R.string.screen_exchange_rate), style = MaterialTheme.typography.titleLarge)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rows) { row ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = row.country, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${stringResource(R.string.label_buy)}: ${row.buy}   ${stringResource(R.string.label_sell)}: ${row.sell}"
                        )
                    }
                }
            }
        }
    }
}

private data class RowData(val country: String, val buy: String, val sell: String)

