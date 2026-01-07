package com.projekt.xvizvary.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.projekt.xvizvary.R

@Composable
fun SignInScreen(
    onSignUp: () -> Unit,
    onSignedIn: () -> Unit
) {
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.screen_sign_in),
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = email,
            onValueChange = setEmail,
            label = { Text(stringResource(R.string.label_email)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = setPassword,
            label = { Text(stringResource(R.string.label_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.action_forgot_password),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSignedIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.action_sign_in))
        }

        Button(
            onClick = onSignUp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.action_sign_up))
        }
    }
}

