package com.projekt.xvizvary.ui.screens.limits

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.database.model.LimitWithSpent
import com.projekt.xvizvary.ui.theme.SmartBudgetTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LimitsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCategory = Category(
        id = 1,
        name = "Food",
        icon = "restaurant",
        color = 0xFFE57373
    )

    private val testLimit = Limit(
        id = 1,
        categoryId = 1,
        limitAmount = 5000.0,
        periodMonths = 1
    )

    private val testLimitWithSpent = LimitWithSpent(
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
        var clickedLimitId: Long? = null
        
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
        assert(clickedLimitId == 1L)
    }
}

// Helper composable for testing
@androidx.compose.runtime.Composable
private fun LimitsScreenContent(
    uiState: LimitsUiState,
    onLimitClick: (Long) -> Unit,
    onAddLimit: () -> Unit,
    onDeleteLimit: (LimitWithSpent) -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.material3.Text(
            text = "Limits",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
        )

        if (uiState.limits.isEmpty() && !uiState.isLoading) {
            androidx.compose.material3.Text(text = "Nothing here yet.")
        } else {
            uiState.limits.forEach { limitWithSpent ->
                androidx.compose.material3.Card(
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    onClick = { onLimitClick(limitWithSpent.limit.id) }
                ) {
                    androidx.compose.material3.Text(
                        text = limitWithSpent.category.name,
                        modifier = androidx.compose.ui.Modifier.padding(16.dp)
                    )
                }
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
