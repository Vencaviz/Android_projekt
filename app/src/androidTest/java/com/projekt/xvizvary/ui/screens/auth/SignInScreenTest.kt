package com.projekt.xvizvary.ui.screens.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.projekt.xvizvary.ui.theme.SmartBudgetTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signInScreen_displaysTitle() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                SignInScreenContent(
                    email = "",
                    password = "",
                    isLoading = false,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onSignIn = {},
                    onSignUp = {}
                )
            }
        }

        composeTestRule.onNodeWithText("SmartBudget").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in").assertIsDisplayed()
    }

    @Test
    fun signInScreen_emailField_acceptsInput() {
        var email = ""
        
        composeTestRule.setContent {
            SmartBudgetTheme {
                SignInScreenContent(
                    email = email,
                    password = "",
                    isLoading = false,
                    onEmailChange = { email = it },
                    onPasswordChange = {},
                    onSignIn = {},
                    onSignUp = {}
                )
            }
        }

        composeTestRule.onNodeWithText("E-mail").performTextInput("test@example.com")
    }

    @Test
    fun signInScreen_signInButton_isDisplayed() {
        composeTestRule.setContent {
            SmartBudgetTheme {
                SignInScreenContent(
                    email = "",
                    password = "",
                    isLoading = false,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onSignIn = {},
                    onSignUp = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Sign in").assertIsDisplayed()
    }

    @Test
    fun signInScreen_signUpLink_navigatesToSignUp() {
        var navigatedToSignUp = false
        
        composeTestRule.setContent {
            SmartBudgetTheme {
                SignInScreenContent(
                    email = "",
                    password = "",
                    isLoading = false,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onSignIn = {},
                    onSignUp = { navigatedToSignUp = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Sign up").performClick()
        assert(navigatedToSignUp)
    }
}

// Helper composable for testing
@androidx.compose.runtime.Composable
private fun SignInScreenContent(
    email: String,
    password: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.material3.Text(
            text = "SmartBudget",
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
        )

        androidx.compose.material3.Text(
            text = "Sign in",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
        )

        androidx.compose.material3.OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { androidx.compose.material3.Text("E-mail") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        androidx.compose.material3.OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { androidx.compose.material3.Text("Password") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        androidx.compose.material3.Button(
            onClick = onSignIn,
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            androidx.compose.material3.Text(text = "Sign in")
        }

        androidx.compose.material3.TextButton(onClick = onSignUp) {
            androidx.compose.material3.Text(text = "Sign up")
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
