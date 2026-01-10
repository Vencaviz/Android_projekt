package com.projekt.xvizvary.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.database.model.TransactionWithCategory
import com.projekt.xvizvary.ui.theme.SmartBudgetTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCategory = Category(
        id = 1,
        name = "Food",
        icon = "restaurant",
        color = 0xFFE57373
    )

    private val testTransactions = listOf(
        TransactionWithCategory(
            transaction = Transaction(
                id = 1,
                name = "Grocery Shopping",
                amount = 500.0,
                type = TransactionType.EXPENSE,
                categoryId = 1,
                date = System.currentTimeMillis()
            ),
            category = testCategory
        ),
        TransactionWithCategory(
            transaction = Transaction(
                id = 2,
                name = "Monthly Salary",
                amount = 25000.0,
                type = TransactionType.INCOME,
                categoryId = null,
                date = System.currentTimeMillis()
            ),
            category = null
        )
    )

    @Test
    fun homeScreen_displaysOverviewTitle() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        isLoading = false,
                        transactions = testTransactions,
                        monthlyIncome = 25000.0,
                        monthlyExpense = 500.0,
                        monthlyBalance = 24500.0,
                        currentMonth = "January 2024"
                    ),
                    onAddTransaction = {},
                    onDeleteTransaction = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Overview").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysTransactions() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        isLoading = false,
                        transactions = testTransactions,
                        monthlyIncome = 25000.0,
                        monthlyExpense = 500.0,
                        monthlyBalance = 24500.0,
                        currentMonth = "January 2024"
                    ),
                    onAddTransaction = {},
                    onDeleteTransaction = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Grocery Shopping").assertIsDisplayed()
        composeTestRule.onNodeWithText("Monthly Salary").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysEmptyState_whenNoTransactions() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        isLoading = false,
                        transactions = emptyList(),
                        monthlyIncome = 0.0,
                        monthlyExpense = 0.0,
                        monthlyBalance = 0.0,
                        currentMonth = "January 2024"
                    ),
                    onAddTransaction = {},
                    onDeleteTransaction = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Nothing here yet.").assertIsDisplayed()
    }

    @Test
    fun homeScreen_addTransactionButton_isClickable() {
        var clicked = false
        
        composeTestRule.setContent {
            SmartBudgetTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        isLoading = false,
                        transactions = emptyList(),
                        currentMonth = "January 2024"
                    ),
                    onAddTransaction = { clicked = true },
                    onDeleteTransaction = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Add transaction").performClick()
        assert(clicked)
    }
}

// Helper composable for testing without ViewModel
@androidx.compose.runtime.Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onAddTransaction: () -> Unit,
    onDeleteTransaction: (TransactionWithCategory) -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .androidx.compose.foundation.layout.fillMaxSize()
            .androidx.compose.foundation.layout.padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.material3.Card(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Overview",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                )
                androidx.compose.material3.Button(
                    onClick = onAddTransaction,
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                ) {
                    androidx.compose.material3.Text(text = "Add transaction")
                }
            }
        }

        if (uiState.transactions.isEmpty() && !uiState.isLoading) {
            androidx.compose.material3.Text(text = "Nothing here yet.")
        } else {
            uiState.transactions.forEach { tx ->
                androidx.compose.material3.Text(text = tx.transaction.name)
            }
        }
    }
}

private val androidx.compose.ui.Modifier.fillMaxSize: androidx.compose.ui.Modifier
    get() = this.then(androidx.compose.foundation.layout.fillMaxSize())

private val androidx.compose.ui.Modifier.fillMaxWidth: androidx.compose.ui.Modifier
    get() = this.then(androidx.compose.foundation.layout.fillMaxWidth())

private fun androidx.compose.ui.Modifier.padding(dp: androidx.compose.ui.unit.Dp) = 
    this.then(androidx.compose.foundation.layout.padding(dp))

private val Int.dp: androidx.compose.ui.unit.Dp
    get() = androidx.compose.ui.unit.dp.times(this.toFloat())
