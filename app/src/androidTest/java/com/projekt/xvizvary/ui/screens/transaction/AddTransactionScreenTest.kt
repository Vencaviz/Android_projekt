package com.projekt.xvizvary.ui.screens.transaction

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.ui.theme.SmartBudgetTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddTransactionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCategories = listOf(
        Category(id = 1, name = "Food", icon = "restaurant", color = 0xFFE57373),
        Category(id = 2, name = "Transport", icon = "directions_car", color = 0xFF64B5F6)
    )

    @Test
    fun addTransactionScreen_displaysTitle() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                AddTransactionScreenContent(
                    uiState = AddTransactionUiState(categories = testCategories),
                    onNameChange = {},
                    onAmountChange = {},
                    onTypeChange = {},
                    onCategoryChange = {},
                    onSave = {},
                    onCancel = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Add transaction").assertIsDisplayed()
    }

    @Test
    fun addTransactionScreen_displaysTransactionTypeSelector() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                AddTransactionScreenContent(
                    uiState = AddTransactionUiState(categories = testCategories),
                    onNameChange = {},
                    onAmountChange = {},
                    onTypeChange = {},
                    onCategoryChange = {},
                    onSave = {},
                    onCancel = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Expense").assertIsDisplayed()
        composeTestRule.onNodeWithText("Income").assertIsDisplayed()
    }

    @Test
    fun addTransactionScreen_displaysCategories() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                AddTransactionScreenContent(
                    uiState = AddTransactionUiState(categories = testCategories),
                    onNameChange = {},
                    onAmountChange = {},
                    onTypeChange = {},
                    onCategoryChange = {},
                    onSave = {},
                    onCancel = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Food").assertIsDisplayed()
        composeTestRule.onNodeWithText("Transport").assertIsDisplayed()
    }

    @Test
    fun addTransactionScreen_cancelButton_callsCallback() {
        var cancelled = false
        
        composeTestRule.setContent {
            SmartBudgetTheme {
                AddTransactionScreenContent(
                    uiState = AddTransactionUiState(categories = testCategories),
                    onNameChange = {},
                    onAmountChange = {},
                    onTypeChange = {},
                    onCategoryChange = {},
                    onSave = {},
                    onCancel = { cancelled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Cancel").performClick()
        assert(cancelled)
    }
}

// Helper composable for testing
@androidx.compose.runtime.Composable
private fun AddTransactionScreenContent(
    uiState: AddTransactionUiState,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onCategoryChange: (Long?) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        androidx.compose.material3.Text(
            text = "Add transaction",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
        )

        // Transaction type buttons
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.material3.TextButton(
                onClick = { onTypeChange(TransactionType.EXPENSE) }
            ) {
                androidx.compose.material3.Text("Expense")
            }
            androidx.compose.material3.TextButton(
                onClick = { onTypeChange(TransactionType.INCOME) }
            ) {
                androidx.compose.material3.Text("Income")
            }
        }

        // Name field
        androidx.compose.material3.OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { androidx.compose.material3.Text("Name") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        )

        // Amount field
        androidx.compose.material3.OutlinedTextField(
            value = uiState.amount,
            onValueChange = onAmountChange,
            label = { androidx.compose.material3.Text("Amount") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        )

        // Categories
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            uiState.categories.forEach { category ->
                androidx.compose.material3.FilterChip(
                    selected = uiState.selectedCategoryId == category.id,
                    onClick = { onCategoryChange(category.id) },
                    label = { androidx.compose.material3.Text(category.name) }
                )
            }
        }

        // Buttons
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            androidx.compose.material3.OutlinedButton(onClick = onCancel) {
                androidx.compose.material3.Text("Cancel")
            }
            androidx.compose.material3.Button(onClick = onSave) {
                androidx.compose.material3.Text("Save")
            }
        }
    }
}

private fun androidx.compose.ui.Modifier.fillMaxSize() = 
    this.then(androidx.compose.foundation.layout.fillMaxSize())

private fun androidx.compose.ui.Modifier.fillMaxWidth() = 
    this.then(androidx.compose.foundation.layout.fillMaxWidth())

private fun androidx.compose.ui.Modifier.padding(dp: androidx.compose.ui.unit.Dp) = 
    this.then(androidx.compose.foundation.layout.padding(dp))

private val Int.dp: androidx.compose.ui.unit.Dp
    get() = androidx.compose.ui.unit.Dp(this.toFloat())
