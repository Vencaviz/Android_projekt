package com.projekt.xvizvary.ui.screens.tools

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.projekt.xvizvary.R
import com.projekt.xvizvary.network.model.HistoricalRate
import com.projekt.xvizvary.network.model.InterestRateDisplay

@Composable
fun InterestRateScreen(
    viewModel: InterestRateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.selectedRate != null) {
                    IconButton(onClick = { viewModel.clearSelection() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
                Text(
                    text = if (uiState.selectedRate != null) 
                        uiState.selectedRate!!.name 
                    else 
                        stringResource(R.string.screen_interest_rate),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            IconButton(onClick = { viewModel.refresh() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.action_refresh)
                )
            }
        }

        when {
            uiState.isLoading && uiState.selectedRate == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.message_error_generic),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.error ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            uiState.selectedRate != null -> {
                InterestRateDetailView(
                    rate = uiState.selectedRate!!,
                    historicalData = uiState.historicalData,
                    isLoading = uiState.isLoading
                )
            }
            uiState.rates.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.message_empty))
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.rates, key = { it.id }) { rate ->
                        InterestRateCard(
                            rate = rate,
                            onClick = { viewModel.selectRate(rate) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InterestRateCard(
    rate: InterestRateDisplay,
    onClick: () -> Unit
) {
    val rateColor = when {
        rate.ratePct > 5 -> Color(0xFFE53935)
        rate.ratePct > 2 -> Color(0xFFFF9800)
        rate.ratePct < 0 -> Color(0xFF2196F3)
        else -> Color(0xFF4CAF50)
    }

    val changeColor = when {
        rate.change > 0 -> Color(0xFFE53935)
        rate.change < 0 -> Color(0xFF4CAF50)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = rate.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${rate.country} (${rate.countryCode})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = rate.formattedRate,
                    style = MaterialTheme.typography.titleLarge,
                    color = rateColor,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            rate.change > 0 -> Icons.AutoMirrored.Filled.TrendingUp
                            rate.change < 0 -> Icons.AutoMirrored.Filled.TrendingDown
                            else -> Icons.Default.Remove
                        },
                        contentDescription = null,
                        tint = changeColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = rate.formattedChange,
                        style = MaterialTheme.typography.bodySmall,
                        color = changeColor
                    )
                }
            }
        }
    }
}

@Composable
private fun InterestRateDetailView(
    rate: InterestRateDisplay,
    historicalData: List<HistoricalRate>,
    isLoading: Boolean
) {
    val rateColor = when {
        rate.ratePct > 5 -> Color(0xFFE53935)
        rate.ratePct > 2 -> Color(0xFFFF9800)
        rate.ratePct < 0 -> Color(0xFF2196F3)
        else -> Color(0xFF4CAF50)
    }

    val changeColor = when {
        rate.change > 0 -> Color(0xFFE53935)
        rate.change < 0 -> Color(0xFF4CAF50)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current Rate Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.label_current_rate),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = rate.formattedRate,
                        style = MaterialTheme.typography.displayMedium,
                        color = rateColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                rate.change > 0 -> Icons.AutoMirrored.Filled.TrendingUp
                                rate.change < 0 -> Icons.AutoMirrored.Filled.TrendingDown
                                else -> Icons.Default.Remove
                            },
                            contentDescription = null,
                            tint = changeColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = rate.formattedChange,
                            style = MaterialTheme.typography.titleMedium,
                            color = changeColor
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${stringResource(R.string.label_last_updated)}: ${rate.lastUpdated}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Info Card
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(
                        label = stringResource(R.string.label_country),
                        value = "${rate.country} (${rate.countryCode})"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(
                        label = stringResource(R.string.label_institution),
                        value = rate.name
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(
                        label = stringResource(R.string.label_type),
                        value = stringResource(R.string.label_central_bank)
                    )
                }
            }
        }

        // Historical Chart
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.label_historical_trend),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.label_last_12_months),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (historicalData.isNotEmpty()) {
                        InterestRateChart(
                            data = historicalData,
                            color = rateColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Chart legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = historicalData.first().label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = historicalData.last().label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InterestRateChart(
    data: List<HistoricalRate>,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val minValue = (data.minOf { it.rate } - 0.5).coerceAtLeast(-1.0)
    val maxValue = data.maxOf { it.rate } + 0.5
    val range = (maxValue - minValue).coerceAtLeast(1.0)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1).coerceAtLeast(1)

        // Draw grid lines
        val gridColor = color.copy(alpha = 0.1f)
        for (i in 0..4) {
            val y = height * i / 4
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw the line chart
        if (data.size > 1) {
            val path = Path()
            data.forEachIndexed { index, point ->
                val x = index * stepX
                val y = height - ((point.rate - minValue) / range * height).toFloat()

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw points
            data.forEachIndexed { index, point ->
                val x = index * stepX
                val y = height - ((point.rate - minValue) / range * height).toFloat()

                drawCircle(
                    color = color,
                    radius = 5.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}
