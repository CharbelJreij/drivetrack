package com.charbel.drivetracker.ui.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.charbel.drivetracker.R
import com.charbel.drivetracker.ui.components.GradientHeroCard
import com.charbel.drivetracker.ui.components.HeroMiniMetric
import com.charbel.drivetracker.ui.components.MetricCard
import com.charbel.drivetracker.ui.components.SectionTitle
import com.charbel.drivetracker.ui.components.WeeklyDistanceChart
import com.charbel.drivetracker.util.formatDistance
import com.charbel.drivetracker.util.formatDuration

@Composable
fun InsightsScreen(
    uiState: InsightsUiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            GradientHeroCard(
                eyebrow = stringResource(R.string.insights),
                title = stringResource(R.string.insights_hero_title),
                subtitle = stringResource(R.string.insights_subtitle),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    HeroMiniMetric(
                        label = stringResource(R.string.this_week),
                        value = formatDistance(uiState.weeklySummary.totalDistanceMeters),
                        modifier = Modifier.weight(1f),
                    )
                    HeroMiniMetric(
                        label = stringResource(R.string.this_month),
                        value = formatDistance(uiState.monthlySummary.totalDistanceMeters),
                        modifier = Modifier.weight(1f),
                    )
                    HeroMiniMetric(
                        label = stringResource(R.string.stats_number_of_drives),
                        value = uiState.weeklySummary.driveCount.toString(),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        item {
            SectionTitle(
                title = stringResource(R.string.insights_chart_title),
                subtitle = stringResource(R.string.weekly_overview),
            )
        }

        item {
            WeeklyDistanceChart(
                bars = uiState.weeklyBars,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            SectionTitle(
                title = stringResource(R.string.this_week),
                subtitle = stringResource(R.string.weekly_overview),
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricCard(
                    label = stringResource(R.string.stats_total_distance),
                    value = formatDistance(uiState.weeklySummary.totalDistanceMeters),
                    modifier = Modifier.weight(1f),
                    supportingText = stringResource(R.string.metric_distance_supporting),
                )
                MetricCard(
                    label = stringResource(R.string.stats_number_of_drives),
                    value = uiState.weeklySummary.driveCount.toString(),
                    modifier = Modifier.weight(1f),
                    supportingText = stringResource(R.string.metric_drive_count_supporting),
                    accentColor = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        item {
            MetricCard(
                label = stringResource(R.string.stats_total_duration),
                value = formatDuration(uiState.weeklySummary.totalDurationSeconds),
                supportingText = stringResource(R.string.metric_duration_supporting),
            )
        }

        item {
            SectionTitle(
                title = stringResource(R.string.this_month),
                subtitle = stringResource(R.string.monthly_overview),
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricCard(
                    label = stringResource(R.string.stats_total_distance),
                    value = formatDistance(uiState.monthlySummary.totalDistanceMeters),
                    modifier = Modifier.weight(1f),
                    supportingText = stringResource(R.string.metric_distance_supporting),
                )
                MetricCard(
                    label = stringResource(R.string.stats_number_of_drives),
                    value = uiState.monthlySummary.driveCount.toString(),
                    modifier = Modifier.weight(1f),
                    supportingText = stringResource(R.string.metric_drive_count_supporting),
                    accentColor = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        item {
            MetricCard(
                label = stringResource(R.string.stats_total_duration),
                value = formatDuration(uiState.monthlySummary.totalDurationSeconds),
                supportingText = stringResource(R.string.metric_duration_supporting),
            )
        }
    }
}
