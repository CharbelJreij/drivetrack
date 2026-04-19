package com.charbel.drivetracker.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.charbel.drivetracker.data.repository.AuthSubmitMode
import com.charbel.drivetracker.ui.components.GradientHeroCard
import com.charbel.drivetracker.ui.components.HeroActionButton
import com.charbel.drivetracker.ui.components.InfoCard
import com.charbel.drivetracker.ui.components.SectionTitle
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onModeChange: (AuthSubmitMode) -> Unit,
    onSubmit: () -> Unit,
    onDismissMessage: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            GradientHeroCard(
                eyebrow = "Account",
                title = "Keep your drives backed up",
                subtitle = "Sign in so your recorded trips stay linked to your account and sync when the internet is available.",
            )
        }

        item {
            SectionTitle(
                title = if (uiState.mode == AuthSubmitMode.SIGN_IN) {
                    "Sign in"
                } else {
                    "Create account"
                },
                subtitle = "Simple email and password auth",
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(
                    onClick = { onModeChange(AuthSubmitMode.SIGN_IN) },
                    enabled = uiState.mode != AuthSubmitMode.SIGN_IN,
                ) {
                    Text(text = "Sign in")
                }
                TextButton(
                    onClick = { onModeChange(AuthSubmitMode.SIGN_UP) },
                    enabled = uiState.mode != AuthSubmitMode.SIGN_UP,
                ) {
                    Text(text = "Register")
                }
            }
        }

        item {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(),
            )
        }

        item {
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(),
            )
        }

        item {
            HeroActionButton(
                text = if (uiState.isLoading) {
                    "Working..."
                } else if (uiState.mode == AuthSubmitMode.SIGN_IN) {
                    "Sign In"
                } else {
                    "Create Account"
                },
                onClick = onSubmit,
            )
        }

        if (uiState.isLoading) {
            item {
                CircularProgressIndicator()
            }
        }

        uiState.message?.let { message ->
            item {
                InfoCard(
                    title = "Auth status",
                    message = message,
                    icon = if (uiState.mode == AuthSubmitMode.SIGN_IN) {
                        Icons.Outlined.Key
                    } else {
                        Icons.Outlined.CloudDone
                    },
                    accentColor = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        if (!uiState.isConfigured) {
            item {
                Text(
                    text = "Online sync is not configured yet. Add the required project keys in local.properties before using account sync.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    LaunchedEffect(uiState.message) {
        if (uiState.message != null) {
            delay(4_000L)
            onDismissMessage()
        }
    }
}
