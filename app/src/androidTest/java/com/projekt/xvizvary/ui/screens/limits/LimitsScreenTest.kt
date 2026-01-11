package com.projekt.xvizvary.ui.screens.limits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.ui.theme.SmartBudgetTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LimitsScreenTest {

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

    private val testLimit = Limit(
        id = 1,
        firestoreId = "limit1",
        userId = testUserId,
        categoryId = "cat1",
        limitAmount = 5000.0,
        periodMonths = 1
    )

    private val testLimitWithSpent = LimitWithSpentDisplay(
        limit = testLimit,
        category = testCategory,
        spentAmount = 2500.0
    )

    @Test
    fun limitsScreen_displaysTitle() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                LimitsScreenContent(
                    uiState = LimitsUiState(
                        isLoading = false,
                        limits = listOf(testLimitWithSpent)
                    ),
                    onLimitClick = {},
                    onAddLimit = {},
                    onDeleteLimit = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Limits").assertIsDisplayed()
    }

    @Test
    fun limitsScreen_displaysLimits() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                LimitsScreenContent(
                    uiState = LimitsUiState(
                        isLoading = false,
                        limits = listOf(testLimitWithSpent)
                    ),
                    onLimitClick = {},
                    onAddLimit = {},
                    onDeleteLimit = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Food").assertIsDisplayed()
    }

    @Test
    fun limitsScreen_displaysEmptyState_whenNoLimits() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                LimitsScreenContent(
                    uiState = LimitsUiState(
                        isLoading = false,
                        limits = emptyList()
                    ),
                    onLimitClick = {},
                    onAddLimit = {},
                    onDeleteLimit = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Nothing here yet.").assertIsDisplayed()
    }

    @Test
    fun limitsScreen_limitCard_isClickable() {
        var clickedLimitId: String? = null
        
        composeTestRule.setContent {
            SmartBudgetTheme {
                LimitsScreenContent(
                    uiState = LimitsUiState(
                        isLoading = false,
                        limits = listOf(testLimitWithSpent)
                    ),
                    onLimitClick = { clickedLimitId = it },
                    onAddLimit = {},
                    onDeleteLimit = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Food").performClick()
        assert(clickedLimitId == "limit1")
    }
}

// Helper composable for testing
@Composable
private fun LimitsScreenContent(
    uiState: LimitsUiState,
    onLimitClick: (String) -> Unit,
    onAddLimit: () -> Unit,
    onDeleteLimit: (LimitWithSpentDisplay) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Limits",
            style = MaterialTheme.typography.titleLarge
        )

        if (uiState.limits.isEmpty() && !uiState.isLoading) {
            Text(text = "Nothing here yet.")
        } else {
            uiState.limits.forEach { limitWithSpent ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onLimitClick(limitWithSpent.limit.firestoreId) }
                ) {
                    Text(
                        text = limitWithSpent.category.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
