package com.charbel.drivetracker.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.charbel.drivetracker.R
import com.charbel.drivetracker.ui.components.GradientHeroCard
import com.charbel.drivetracker.ui.components.HeroActionButton
import com.charbel.drivetracker.ui.components.InfoCard
import com.charbel.drivetracker.ui.components.rememberConnectionStatus
import com.charbel.drivetracker.ui.components.SectionTitle

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onSignOut: () -> Unit,
) {
    val isOnline = rememberConnectionStatus()
    val syncTitle = when {
        uiState.pendingTripCount > 0 && !isOnline -> stringResource(R.string.profile_sync_awaiting_title)
        uiState.pendingTripCount > 0 -> stringResource(R.string.profile_sync_in_progress_title)
        isOnline -> stringResource(R.string.profile_sync_complete_title)
        else -> stringResource(R.string.offline)
    }
    val syncMessage = when {
        uiState.pendingTripCount > 0 && !isOnline -> {
            stringResource(R.string.profile_sync_awaiting_message, uiState.pendingTripCount)
        }
        uiState.pendingTripCount > 0 -> {
            stringResource(R.string.profile_sync_in_progress_message, uiState.pendingTripCount)
        }
        isOnline -> stringResource(R.string.profile_sync_complete_message)
        else -> stringResource(R.string.profile_sync_offline_message)
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            GradientHeroCard(
                eyebrow = stringResource(R.string.profile),
                title = stringResource(R.string.profile_title),
                subtitle = stringResource(R.string.profile_subtitle),
            ) {
                HeroActionButton(
                    text = stringResource(R.string.sign_out),
                    onClick = onSignOut,
                )
            }
        }

        item {
            SectionTitle(
                title = stringResource(R.string.profile_current_account),
                subtitle = stringResource(R.string.profile_switch_account_note),
            )
        }

        item {
            InfoCard(
                title = uiState.email ?: stringResource(R.string.profile_not_signed_in),
                message = stringResource(R.string.profile_current_account_message),
                icon = Icons.Outlined.Person,
            )
        }

        item {
            InfoCard(
                title = syncTitle,
                message = syncMessage,
                icon = Icons.Outlined.CloudDone,
            )
        }
    }
}
