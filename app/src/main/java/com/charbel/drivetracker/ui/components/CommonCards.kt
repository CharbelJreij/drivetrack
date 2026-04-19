package com.charbel.drivetracker.ui.components

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.charbel.drivetracker.R
import com.charbel.drivetracker.model.SyncStatus
import com.charbel.drivetracker.ui.theme.HazardRed
import com.charbel.drivetracker.ui.theme.TaxiAmber

@Composable
fun GradientHeroCard(
    eyebrow: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Text(
                        text = eyebrow.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                if (trailing != null) trailing()
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            content()
        }
    }
}

@Composable
fun HeroMiniMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun HeroActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
fun InlineMetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    accentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        modifier = modifier.animateContentSize(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                )
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (!supportingText.isNullOrBlank()) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(10.dp),
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (trailing != null) trailing()
        }
    }
}

@Composable
fun StatusMessageCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    InfoCard(
        title = stringResource(R.string.trip_summary),
        message = message,
        modifier = modifier,
        icon = Icons.Outlined.Info,
        accentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
fun InfoCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Info,
    accentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun RouteStopsCard(
    startLabel: String,
    startValue: String,
    endLabel: String,
    endValue: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(
                modifier = Modifier.padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface),
                )
                Box(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .width(2.dp)
                        .height(28.dp)
                        .background(MaterialTheme.colorScheme.outline),
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RouteStopRow(label = startLabel, value = startValue)
                RouteStopRow(label = endLabel, value = endValue)
            }
        }
    }
}

@Composable
private fun RouteStopRow(
    label: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun SyncStatusChip(
    syncStatus: SyncStatus,
    modifier: Modifier = Modifier,
) {
    val (label, color, background) = when (syncStatus) {
        SyncStatus.PENDING -> Triple(
            stringResource(R.string.sync_status_pending),
            TaxiAmber,
            TaxiAmber.copy(alpha = 0.14f),
        )
        SyncStatus.SYNCED -> Triple(
            stringResource(R.string.sync_status_synced),
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
        )
        SyncStatus.FAILED -> Triple(
            stringResource(R.string.sync_status_failed),
            HazardRed,
            HazardRed.copy(alpha = 0.12f),
        )
        SyncStatus.LOCAL_ONLY -> Triple(
            stringResource(R.string.sync_status_local_only),
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant,
        )
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.2f),
                shape = CircleShape,
            )
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = color,
        )
    }
}

@Composable
fun ConnectionStatusPill(
    modifier: Modifier = Modifier,
) {
    val isOnline = rememberConnectionStatus()

    val label = if (isOnline) {
        stringResource(R.string.online)
    } else {
        stringResource(R.string.offline)
    }
    val color = if (isOnline) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val background = if (isOnline) {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.2f),
                shape = CircleShape,
            )
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = color,
        )
    }
}

@Composable
fun rememberConnectionStatus(): Boolean {
    val context = LocalContext.current
    val connectivityManager = remember(context) {
        context.getSystemService(ConnectivityManager::class.java)
    }

    var isOnline by remember(connectivityManager) {
        mutableStateOf(connectivityManager?.isCurrentlyOnline() == true)
    }

    DisposableEffect(connectivityManager) {
        if (connectivityManager == null) {
            onDispose { }
        } else {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isOnline = connectivityManager.isCurrentlyOnline()
                }

                override fun onLost(network: Network) {
                    isOnline = connectivityManager.isCurrentlyOnline()
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities,
                ) {
                    isOnline = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            onDispose {
                runCatching {
                    connectivityManager.unregisterNetworkCallback(callback)
                }
            }
        }
    }

    return isOnline
}

private fun ConnectivityManager.isCurrentlyOnline(): Boolean {
    val capabilities = getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
