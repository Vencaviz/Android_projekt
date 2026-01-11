package com.projekt.xvizvary.ui.screens.limits

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.projekt.xvizvary.R
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.util.CurrencyUtils
import com.projekt.xvizvary.util.DateUtils

@Composable
fun LimitDetailScreen(
    onLimitSaved: () -> Unit,
    onCancel: () -> Unit,
    viewModel: LimitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LimitDetailEvent.LimitSaved -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.message_limit_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                    onLimitSaved()
                }
                is LimitDetailEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.isAddMode -> {
            AddLimitContent(
                uiState = uiState,
                viewModel = viewModel,
                onCancel = onCancel
            )
        }
        else -> {
            LimitDetailContent(
                uiState = uiState,
                onBack = onCancel
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddLimitContent(
    uiState: LimitDetailUiState,
    viewModel: LimitDetailViewModel,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.title_add_limit),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(R.string.label_category),
            style = MaterialTheme.typography.labelLarge
        )

        if (uiState.categoryError != null) {
            Text(
                text = stringResource(R.string.error_select_category),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (uiState.availableCategories.isEmpty()) {
            Text(
                text = stringResource(R.string.message_all_categories_have_limits),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.availableCategories.forEach { category ->
                    val isSelected = uiState.selectedCategoryId == category.firestoreId
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.onCategoryChange(
                                if (isSelected) null else category.firestoreId
                            )
                        },
                        label = { Text(category.name) }
                    )
                }
            }
        }

        OutlinedTextField(
            value = uiState.limitAmount,
            onValueChange = viewModel::onAmountChange,
            label = { Text(stringResource(R.string.label_limit_amount)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = uiState.amountError != null,
            supportingText = uiState.amountError?.let {
                { Text(stringResource(R.string.error_invalid_amount)) }
            },
            suffix = { Text("Kč") },
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.action_cancel))
            }

            Button(
                onClick = viewModel::saveLimit,
                modifier = Modifier.weight(1f),
                enabled = !uiState.isLoading && uiState.availableCategories.isNotEmpty()
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

@Composable
private fun LimitDetailContent(
    uiState: LimitDetailUiState,
    onBack: () -> Unit
) {
    val limit = uiState.limit ?: return
    val category = uiState.category
    
    val progress = if (limit.limitAmount > 0) {
        (uiState.spentAmount / limit.limitAmount).toFloat().coerceIn(0f, 1f)
    } else 0f
    
    val isOverBudget = uiState.spentAmount > limit.limitAmount
    
    val progressColor = when {
        isOverBudget -> Color(0xFFE53935)
        progress > 0.8f -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
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
                        text = category?.name ?: stringResource(R.string.label_uncategorized),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = CurrencyUtils.formatCzk(uiState.spentAmount),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                    
                    Text(
                        text = stringResource(
                            R.string.label_spent_of_limit,
                            "",
                            CurrencyUtils.formatCzk(limit.limitAmount)
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = progressColor,
                        trackColor = progressColor.copy(alpha = 0.2f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val remaining = limit.limitAmount - uiState.spentAmount
                    Text(
                        text = if (remaining >= 0) {
                            stringResource(R.string.label_remaining, CurrencyUtils.formatCzk(remaining))
                        } else {
                            stringResource(R.string.label_over_budget, CurrencyUtils.formatCzk(-remaining))
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = progressColor
                    )
                }
            }
        }

        // Daily Spending Chart
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.label_daily_spending),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = DateUtils.getCurrentMonthName(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (uiState.dailySpending.isNotEmpty()) {
                        DailySpendingChart(
                            data = uiState.dailySpending,
                            color = progressColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.message_no_spending_data),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Transactions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.title_transactions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${uiState.transactions.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Transactions List
        if (uiState.transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.message_no_transactions_in_category),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(uiState.transactions, key = { it.firestoreId.ifEmpty { it.id.toString() } }) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
        
        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                        .background(Color(0xFFE53935).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = DateUtils.formatDate(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = "-${CurrencyUtils.formatCzk(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935)
            )
        }
    }
}

@Composable
private fun DailySpendingChart(
    data: List<DailySpending>,
    color: Color,
    modifier: Modifier = Modifier
) {
    val maxAmount = data.maxOfOrNull { it.amount } ?: 1.0

    Canvas(modifier = modifier) {
        val barWidth = size.width / data.size * 0.7f
        val spacing = size.width / data.size * 0.3f
        val maxHeight = size.height * 0.9f

        data.forEachIndexed { index, spending ->
            if (spending.amount > 0) {
                val barHeight = (spending.amount / maxAmount * maxHeight).toFloat()
                val x = index * (barWidth + spacing) + spacing / 2
                val y = size.height - barHeight

                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                )
            }
        }
    }
}
