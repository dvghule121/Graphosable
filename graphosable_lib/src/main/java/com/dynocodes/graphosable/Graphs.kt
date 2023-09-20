package com.dynocodes.graphosable

import android.content.Context
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin

/**
 * A class containing utility functions for creating different types of graphs using Jetpack Compose.
 */
class Graphs {

    /**
     * Composable function for creating a pie chart with labels.
     *
     * @param data List of [Slice] objects representing data for the pie chart.
     * @param modifier Modifier for customizing the layout of the pie chart.
     * @param context Android application context.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Composable
    fun PieChartWithLabels(
        data: List<Slice>,
        modifier: Modifier = Modifier,
        context: Context,
        textColor: Int = Color.White.hashCode(),
        labeltextSize: Int = 14,
        ringSize: Float = 25f
    ) {
        val total = data.sumBy { it.value }
        var startAngle = -90f

        Canvas(modifier = modifier.padding(8.dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = ((minOf(canvasWidth, canvasHeight) / 2) * 0.5f)
            val centerX = canvasWidth / 2
            val centerY = canvasHeight / 2
            val oval = Rect(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

            data.forEach { slice ->
                val sweepAngle = (slice.value / total.toFloat()) * 360f
                drawArc(
                    color = Color(slice.color),
                    style = Stroke(ringSize),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    size = Size(radius * 2, radius * 2),
                    topLeft = Offset(centerX - radius, centerY - radius)
                )

                // Calculate the position of the text label
                val labelRadius = radius * 1.55f
                val labelX = centerX + labelRadius * cos(Math.toRadians((startAngle + sweepAngle / 2f).toDouble()).toFloat())
                val labelY = centerY + labelRadius * sin(Math.toRadians((startAngle + sweepAngle / 2f).toDouble()).toFloat())

                // Draw the text label
                drawIntoCanvas {
                    val paint = Paint().apply {
                        color = textColor
                        textSize = labeltextSize.sp.toPx()
                        textAlign = Paint.Align.CENTER
                    }
                    it.nativeCanvas.drawText(slice.label, labelX, labelY, paint)
                    it.nativeCanvas.drawText("Total", centerX, centerY-labeltextSize, paint)
                    paint.textSize = 12.sp.toPx()
                    it.nativeCanvas.drawText(total.toLong().toString(), centerX, centerY+30, paint)
                    val avg = data.sumBy { it.value } / data.size
                    if (slice.value > avg * 0.25) {
                        it.nativeCanvas.drawText(slice.value.toLong().toString(), labelX, labelY + 50, paint)
                    }
                }

                startAngle += sweepAngle
            }
        }
    }


    /**
     * Composable function for creating a line chart.
     *
     * @param modifier Modifier for customizing the layout of the line chart.
     * @param data List of data points for the line chart.
     * @param labels List of labels for the x-axis.
     * @param unit Unit of measurement for the y-axis.
     */
    @Composable
    fun LineChart(
        modifier: Modifier,
        data: List<Float>,
        labels: List<String>,
        unit: String
    ) {
        val maxValue = data.maxOrNull() ?: 0f
        val padding = 36.dp

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Draw the x-axis
            drawLine(
                start = Offset(0f, size.height - padding.toPx()),
                end = Offset(size.width, size.height - padding.toPx()),
                color = Color.Gray,
                strokeWidth = 2f
            )

            // Draw the y-axis
            drawLine(
                start = Offset(padding.toPx(), 0f),
                end = Offset(padding.toPx(), size.height - padding.toPx()),
                color = Color.Gray,
                strokeWidth = 2f
            )

            // Draw the data points and lines
            val xStep = (size.width - padding.toPx() * 2) / (data.size - 1)
            val yStep = (size.height - padding.toPx() * 2) / maxValue
            val path = Path()
            path.moveTo(padding.toPx(), size.height - padding.toPx() - data[0] * yStep)
            for (i in 1 until data.size) {
                val x = padding.toPx() + i * xStep
                val y = size.height - padding.toPx() - data[i] * yStep
                path.lineTo(x, y)
                drawCircle(color = Color.Blue, radius = 8f, center = Offset(x, y))
            }
            drawPath(
                path = path,
                color = Color.Blue,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )

            // Draw the labels
            val labelXStep = (size.width - padding.toPx() * 2) / (labels.size - 1)
            for (i in labels.indices) {
                val x = padding.toPx() + i * labelXStep
                drawLine(
                    start = Offset(x, size.height - padding.toPx()),
                    end = Offset(x, size.height - padding.toPx() + 16.dp.toPx()),
                    color = Color.Gray,
                    strokeWidth = 2f
                )
                drawIntoCanvas {
                    val paint = Paint().apply {
                        color = Color.White.hashCode()
                        textSize = 12.sp.toPx()
                        textAlign = Paint.Align.CENTER
                    }
                    it.nativeCanvas.drawText(labels[i], x, size.height - padding.toPx() + 28.dp.toPx(), paint)
                    it.nativeCanvas.drawText(unit, (size.width.absoluteValue).dp.toPx(), padding.toPx() / 4, paint)
                    // Draw the values
                    for (i in data.indices) {
                        val x = padding.toPx() + i * xStep
                        val y = size.height - padding.toPx() - data[i] * yStep
                        it.nativeCanvas.drawText(String.format("%.1f", data[i]), x - 1.dp.toPx(), y - 10.dp.toPx(), paint)
                    }
                    val value = maxValue * i / 6
                    val y = size.height - padding.toPx() - value * yStep
                    val text = String.format("%.1f", value)
                    val xnew = padding.toPx() - 16.dp.toPx()
                    val ynew = y - 8.dp.toPx()
                    drawLine(
                        start = Offset(padding.toPx() - 16.dp.toPx(), y),
                        end = Offset(padding.toPx(), y),
                        color = Color.Gray,
                        strokeWidth = 2f
                    )
                    it.nativeCanvas.drawText(text, xnew, ynew, paint)
                }
            }
        }
    }

    /**
     * Composable function for creating a bar chart.
     *
     * @param barDataList List of [BarData] objects containing values and labels for the bar chart.
     * @param modifier Modifier for customizing the layout of the bar chart.
     * @param maincolor Color for the bars in the chart.
     */
    @Composable
    fun BarChart(
        barDataList: List<BarData>,
        modifier: Modifier,
        maincolor: Color = Color.LightGray,
        unit: String = " ",
        textColor: Int =Color.White.hashCode()
    ) {
        // Calculate the maximum value across all BarData objects
        var maxValue = barDataList.map { it.value }.maxOrNull()?: 0f

        maxValue = calculateMagnitude(maxValue)

        val padding = 16.dp
        Row(modifier = modifier.fillMaxSize()) {

            Canvas(modifier = Modifier.fillMaxHeight().padding(padding+12.dp, padding, padding, padding)){
                drawIntoCanvas {

                    val paint = Paint().apply {
                        color = textColor
                        textSize = 12.sp.toPx()
                        textAlign = Paint.Align.CENTER
                    }


                    val yStep = (size.height - padding.toPx() * 2) / maxValue
                    // Draw the y-axis
                    drawLine(
                        start = Offset(padding.toPx(), 0f),
                        end = Offset(padding.toPx(), size.height + padding.toPx()),
                        color = Color.Gray,
                        strokeWidth = 2f
                    )

                    // Draw points on y-axis
                    for (i in 0..4) {
                        val value = maxValue * i / 4
                        val y = size.height - padding.toPx() - value * yStep
                        val text = String.format("%.1f $unit", value)
                        it.nativeCanvas.drawText(text, padding.toPx() - 16.dp.toPx(), y - 8, paint)
                        drawLine(
                            start = Offset(-padding.toPx(), y),
                            end = Offset(size.width + padding.toPx(), y),
                            color = Color.Gray,
                            strokeWidth = 2f
                        )
                    }
                }


            }
            Canvas(modifier = modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxHeight()
                .requiredWidth((55 * barDataList.size).dp)
                .padding(4.dp, padding)) {
                drawIntoCanvas {
                    val paint = Paint().apply {
                        color = textColor
                        textSize = 12.sp.toPx()
                        textAlign = Paint.Align.CENTER
                    }

                    val xStep = 130
                    val yStep = (size.height - padding.toPx() * 2) / maxValue

                    // Draw points on y-axis
                    for (i in 0..4) {
                        val value = maxValue * i / 4
                        val y = size.height - padding.toPx() - value * yStep
                        val text = String.format("%.1f $unit", value)

                        drawLine(
                            start = Offset(-padding.toPx(), y),
                            end = Offset(size.width + padding.toPx(), y),
                            color = Color.Gray,
                            strokeWidth = 2f
                        )
                    }


                    // Draw the x-axis
                    drawLine(
                        start = Offset(0f, size.height - padding.toPx()),
                        end = Offset(size.width, size.height - padding.toPx()),
                        color = Color.Gray,
                        strokeWidth = 2f
                    )

                    // Draw x-axis labels
                    for (i in barDataList.indices) {
                        val x = (2* padding.toPx() + i * xStep) + 8
                        it.nativeCanvas.drawText(
                            barDataList[i].label,
                            x - padding.toPx()+12,
                            size.height - padding.toPx() + 16.dp.toPx(),
                            paint
                        )
                    }


                    // Draw the bars
                    for (i in barDataList.indices) {
                        val x =  i * xStep
                        val barWidth = 25.dp.toPx()
                        val barHeight = barDataList[i].value * yStep
                        drawRoundRect(
                            color = maincolor,
                            topLeft = Offset(
                                x + (xStep - barWidth) / 2,
                                size.height - padding.toPx() - barHeight
                            ),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(12f, 14f)
                        )
                    }

                    // Draw the values
                    for (i in barDataList.indices) {
                        val x = (i * xStep )
                        val y = size.height - padding.toPx() - barDataList[i].value * yStep
                        val text = String.format("%.1f", barDataList[i].value)
                        val xnew = x + padding.toPx() + 22
                        val ynew = y - 8.dp.toPx()
                        it.nativeCanvas.drawText(text, xnew, ynew, paint)
                    }
                }
            }
        }

    }

    fun calculateMagnitude(value: Float): Float {
        if (value <= 0f) return 0f // Handle non-positive values

        val magnitude = 10.0.pow(log10(value.toDouble()).toInt()).toFloat()
        val nextMagnitude = magnitude * 10

        return if (value <= magnitude * 5) magnitude else nextMagnitude
    }

    fun generateRandomColor(): Int{
        return (Math.random() * 16777215).toInt() or (0xFF shl 24)
    }


}

/**
 * Data class representing values and labels for a bar chart.
 *
 * @property values List of integer values representing data for the bars.
 * @property labels List of strings representing labels for the bars.
 */
data class BarData(var value: Float, var label: String)

/**
 * Data class representing a slice in a pie chart.
 *
 * @property value The value of the slice.
 * @property label The label for the slice.
 * @property color The color of the slice.
 */
data class Slice(val value: Int, val label: String = "lol", val color: Int= Graphs().generateRandomColor())
