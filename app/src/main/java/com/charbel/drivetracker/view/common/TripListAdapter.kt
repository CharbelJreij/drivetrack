package com.charbel.drivetracker.view.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charbel.drivetracker.R
import com.charbel.drivetracker.databinding.ItemTripBinding
import com.charbel.drivetracker.model.SyncStatus
import com.charbel.drivetracker.model.Trip
import com.charbel.drivetracker.util.formatDistance
import com.charbel.drivetracker.util.formatDuration
import com.charbel.drivetracker.util.formatSpeed
import com.charbel.drivetracker.util.formatTripDate

class TripListAdapter(
    private val onTripClicked: (Trip) -> Unit,
) : ListAdapter<Trip, TripListAdapter.TripViewHolder>(TripDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding, onTripClicked)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TripViewHolder(
        private val binding: ItemTripBinding,
        private val onTripClicked: (Trip) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip) = with(binding) {
            root.setOnClickListener { onTripClicked(trip) }
            titleText.text = trip.title
            dateText.text = formatTripDate(trip.startedAtMillis)
            startText.text = trip.startAddress ?: root.context.getString(R.string.address_pending)
            endText.text = trip.endAddress ?: root.context.getString(R.string.address_pending)
            distanceValue.text = formatDistance(trip.distanceMeters)
            durationValue.text = formatDuration(trip.durationSeconds)
            speedValue.text = formatSpeed(trip.averageSpeedKmh)
            syncChip.text = when (trip.syncStatus) {
                SyncStatus.PENDING -> root.context.getString(R.string.sync_status_pending)
                SyncStatus.SYNCED -> root.context.getString(R.string.sync_status_synced)
                SyncStatus.FAILED -> root.context.getString(R.string.sync_status_failed)
                SyncStatus.LOCAL_ONLY -> root.context.getString(R.string.sync_status_local_only)
            }
            syncChip.setChipBackgroundColorResource(
                when (trip.syncStatus) {
                    SyncStatus.PENDING -> R.color.sync_chip_pending
                    SyncStatus.SYNCED -> R.color.sync_chip_synced
                    SyncStatus.FAILED -> R.color.sync_chip_failed
                    SyncStatus.LOCAL_ONLY -> R.color.sync_chip_local
                },
            )
            syncChip.setTextColor(ContextCompat.getColor(root.context, R.color.uber_white))
            syncChip.isVisible = true
        }
    }
}

private object TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
    override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean = oldItem == newItem
}
