package com.charbel.drivetracker.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.charbel.drivetracker.R
import com.charbel.drivetracker.model.WeeklyDistanceBar
import kotlin.math.max

class WeeklyDistanceChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.route_accent)
    }

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.route_grid)
        strokeWidth = resources.displayMetrics.density
    }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.route_primary)
        textSize = 12f * resources.displayMetrics.scaledDensity
        textAlign = Paint.Align.CENTER
    }

    private var bars: List<WeeklyDistanceBar> = emptyList()

    fun setBars(newBars: List<WeeklyDistanceBar>) {
        bars = newBars
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val chartBottom = height - (28f * resources.displayMetrics.density)
        canvas.drawLine(
            0f,
            chartBottom,
            width.toFloat(),
            chartBottom,
            axisPaint,
        )

        if (bars.isEmpty()) return

        val maxDistance = max(bars.maxOf { it.distanceMeters }, 1.0)
        val spacing = width / (bars.size * 2f)
        val usableHeight = chartBottom - (12f * resources.displayMetrics.density)

        bars.forEachIndexed { index, bar ->
            val centerX = spacing + (index * spacing * 2f) + spacing / 2f
            val barHeight = ((bar.distanceMeters / maxDistance) * usableHeight).toFloat()
            canvas.drawRoundRect(
                centerX - spacing / 2f,
                chartBottom - barHeight,
                centerX + spacing / 2f,
                chartBottom,
                12f,
                12f,
                barPaint,
            )
            canvas.drawText(
                bar.label,
                centerX,
                height - 8f * resources.displayMetrics.density,
                textPaint,
            )
        }
    }
}
