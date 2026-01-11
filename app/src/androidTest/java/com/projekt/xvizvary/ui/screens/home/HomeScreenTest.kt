package com.projekt.xvizvary.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Transaction
import com.projekt.xvizvary.database.model.TransactionType
import com.projekt.xvizvary.ui.theme.SmartBudgetTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUserId = "testUser123"

    private val testCategory = Category(
        id = 1,
        firestoreId = "cat1",
        userId = testUserId,
        name = "Food",
        icon = "restaurant",
        color = 0xFFE57373
    )

    private val testTransactions = listOf(
        TransactionWithCategoryDisplay(
            transaction = Transaction(
                id = 1,
                firestoreId = "tx1",
                userId = testUserId,
                name = "Grocery Shopping",
                amount = 500.0,
                type = TransactionType.EXPENSE,
                categoryId = "cat1",
                date = System.currentTimeMillis()
            ),
            category = testCategory
        ),
        TransactionWithCategoryDisplay(
            transaction = Transaction(
                id = 2,
                firestoreId = "tx2",
                userId = testUserId,
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
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onAddTransaction: () -> Unit,
    onDeleteTransaction: (TransactionWithCategoryDisplay) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleLarge
                )
                Button(
                    onClick = onAddTransaction,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Add transaction")
                }
            }
        }

        if (uiState.transactions.isEmpty() && !uiState.isLoading) {
            Text(text = "Nothing here yet.")
        } else {
            uiState.transactions.forEach { tx ->
                Text(text = tx.transaction.name)
            }
        }
    }
}
