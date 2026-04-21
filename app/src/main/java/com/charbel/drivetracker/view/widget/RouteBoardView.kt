package com.charbel.drivetracker.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.charbel.drivetracker.R
import com.charbel.drivetracker.model.TripPoint
import kotlin.math.max

class RouteBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.route_grid)
        strokeWidth = resources.displayMetrics.density
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(12f, 12f), 0f)
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#14000000")
        strokeWidth = 10f * resources.displayMetrics.density
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val routePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.route_primary)
        strokeWidth = 5f * resources.displayMetrics.density
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val startPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.route_primary)
        style = Paint.Style.FILL
    }

    private val endPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.route_accent)
        style = Paint.Style.FILL
    }

    private var points: List<TripPoint> = emptyList()

    fun setPoints(newPoints: List<TripPoint>) {
        points = newPoints
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)

        val routeOffsets = projectRouteOffsets(
            width = width.toFloat(),
            height = height.toFloat(),
            padding = 28f * resources.displayMetrics.density,
        )

        if (routeOffsets.size == 1) {
            val center = routeOffsets.first()
            canvas.drawCircle(center.first, center.second, 8f * resources.displayMetrics.density, startPaint)
            return
        }

        if (routeOffsets.isEmpty()) return

        val path = Path().apply {
            routeOffsets.forEachIndexed { index, point ->
                if (index == 0) moveTo(point.first, point.second) else lineTo(point.first, point.second)
            }
        }

        canvas.drawPath(path, shadowPaint)
        canvas.drawPath(path, routePaint)
        val radius = 7f * resources.displayMetrics.density
        canvas.drawCircle(routeOffsets.first().first, routeOffsets.first().second, radius, startPaint)
        canvas.drawCircle(routeOffsets.last().first, routeOffsets.last().second, radius, endPaint)
    }

    private fun drawGrid(canvas: Canvas) {
        val horizontalStep = width / 5f
        val verticalStep = height / 5f

        repeat(4) { index ->
            val x = horizontalStep * (index + 1)
            val y = verticalStep * (index + 1)
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
        }
    }

    private fun projectRouteOffsets(
        width: Float,
        height: Float,
        padding: Float,
    ): List<Pair<Float, Float>> {
        val minLatitude = points.minOfOrNull { it.latitude } ?: return emptyList()
        val maxLatitude = points.maxOfOrNull { it.latitude } ?: return emptyList()
        val minLongitude = points.minOfOrNull { it.longitude } ?: return emptyList()
        val maxLongitude = points.maxOfOrNull { it.longitude } ?: return emptyList()
        val usableWidth = max(width - padding * 2f, 1f)
        val usableHeight = max(height - padding * 2f, 1f)
        val latitudeRange = (maxLatitude - minLatitude).takeIf { it > 0.0 } ?: 0.0
        val longitudeRange = (maxLongitude - minLongitude).takeIf { it > 0.0 } ?: 0.0

        return points.map { point ->
            val xRatio = if (longitudeRange == 0.0) 0.5f else ((point.longitude - minLongitude) / longitudeRange).toFloat()
            val yRatio = if (latitudeRange == 0.0) 0.5f else ((point.latitude - minLatitude) / latitudeRange).toFloat()

            Pair(
                padding + (xRatio * usableWidth),
                height - padding - (yRatio * usableHeight),
            )
        }
    }
}
