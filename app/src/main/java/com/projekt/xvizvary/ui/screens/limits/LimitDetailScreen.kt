package com.projekt.xvizvary.ui.screens.limits

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.projekt.xvizvary.R

@OptIn(ExperimentalLayoutApi::class)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(
                if (uiState.isEditMode) R.string.title_edit_limit 
                else R.string.title_add_limit
            ),
            style = MaterialTheme.typography.titleLarge
        )

        // Category selection
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

        val categoriesToShow = if (uiState.isEditMode) {
            uiState.categories
        } else {
            uiState.availableCategories
        }

        if (categoriesToShow.isEmpty() && !uiState.isLoading) {
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
                categoriesToShow.forEach { category ->
                    FilterChip(
                        selected = uiState.selectedCategoryId == category.id,
                        onClick = {
                            viewModel.onCategoryChange(
                                if (uiState.selectedCategoryId == category.id) null 
                                else category.id
                            )
                        },
                        label = { Text(category.name) },
                        border = BorderStroke(
                            1.dp,
                            if (uiState.selectedCategoryId == category.id)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline
                        ),
                        enabled = !uiState.isEditMode || category.id == uiState.selectedCategoryId
                    )
                }
            }
        }

        // Limit Amount
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

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
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
                enabled = !uiState.isLoading && categoriesToShow.isNotEmpty()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

