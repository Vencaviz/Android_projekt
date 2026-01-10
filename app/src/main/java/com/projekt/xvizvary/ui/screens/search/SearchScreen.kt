package com.projekt.xvizvary.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.projekt.xvizvary.R

@Composable
fun SearchScreen(
    onAtmMap: () -> Unit,
    onExchangeRate: () -> Unit,
    onInterestRate: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.title_what_to_find),
            style = MaterialTheme.typography.titleLarge
        )

        SearchItem(
            title = stringResource(R.string.search_for_atms),
            subtitle = stringResource(R.string.screen_search),
            onClick = onAtmMap
        )
        SearchItem(
            title = stringResource(R.string.search_for_interest_rate),
            subtitle = stringResource(R.string.screen_interest_rate),
            onClick = onInterestRate
        )
        SearchItem(
            title = stringResource(R.string.search_for_exchange_rate),
            subtitle = stringResource(R.string.screen_exchange_rate),
            onClick = onExchangeRate
        )
    }
}

@Composable
private fun SearchItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

