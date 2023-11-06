package com.dynocodes.graphosable


import android.graphics.Paint
import android.graphics.PointF
import android.graphics.fonts.FontStyle
import android.os.Build
import android.support.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.blue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LineChart(
    lineDataList: List<LineData>,
    modifier: Modifier,
    lineChartOptions: LineChartOptions = LineChartOptions(),
    textColor: Color = Color.White
) {
    var maxValue = lineDataList.maxBy { it.value }.value

    maxValue = Graphs().calculateMagnitude(maxValue.toInt()).toFloat()
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    lineChartOptions.internalPadding
                )
        ) {
            val yStep =
                (size.height - lineChartOptions.internalPadding.toPx() * 2) / maxValue
            if (lineChartOptions.drawYLables) drawYAxisLabel(
                yStep,
                maxValue.toInt(),
                lineChartOptions
            )
            if (lineChartOptions.drawYAxis) drawYAxis(lineChartOptions)
        }

        Canvas(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .requiredWidth((lineChartOptions.lineWidth * lineDataList.size)+16.dp)
                .fillMaxHeight()
                .padding(24.dp, lineChartOptions.internalPadding)
        ) {
            val xStep = lineChartOptions.lineWidth.toPx()
            val yStep = (size.height - lineChartOptions.internalPadding.toPx() * 2) / maxValue
            if (lineChartOptions.drawXAxis) drawXAxis(lineDataList, xStep, lineChartOptions)

//            drawLineChart(lineDataList, xStep, yStep, maxValue.toInt(), lineChartOptions)
            drawSmoothLineChart(
                lineDataList,
                xStep,
                yStep,
                maxValue.toInt(),
                lineChartOptions
            )
        }
    }
}

private fun DrawScope.drawLineChart(
    lineDataList: List<LineData>,
    xStep: Float,
    yStep: Float,
    maxValue: Int,
    lineChartOptions: LineChartOptions
) {


    val xStep = lineChartOptions.lineWidth.toPx()
    val yStep = (size.height - lineChartOptions.internalPadding.toPx() * 2) / maxValue
    val path = Path()
    path.moveTo(
        0f,
        size.height - lineChartOptions.internalPadding.toPx() - lineDataList[0].value * yStep
    )


    for (i in 1..lineDataList.size - 1) {

        // Draw the data points and lines

        val x = i * (lineChartOptions.lineWidth.toPx())
        val y =
            size.height - lineChartOptions.internalPadding.toPx() - lineDataList[i].value * yStep
        path.lineTo(x, y)
        drawCircle(color = Color.Blue, radius = 8f, center = Offset(x, y))
    }


    drawPath(
        path = path,
        color = Color.Blue,
        style = Stroke(width = 4f, cap = StrokeCap.Round)
    )




    drawXAxisLabels(lineDataList, xStep, lineChartOptions, lineChartOptions.textColor)


}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
private fun DrawScope.drawSmoothLineChart(
    lineDataList: List<LineData>,
    xStep: Float,
    yStep: Float,
    maxValue: Int,
    lineChartOptions: LineChartOptions
) {
    val path = Path()
    val controlFactor = 0.7f // Adjust this value for smoother or sharper curves

    path.moveTo(
        0f,
        size.height - lineChartOptions.internalPadding.toPx() -
                lineDataList[0].value * yStep
    )

    for (i in 1 until lineDataList.size) {
        val x = i * xStep
        val y = size.height - lineChartOptions.internalPadding.toPx() -
                lineDataList[i].value * yStep

        val x0 = (i - 1) * xStep
        val y0 = size.height - lineChartOptions.internalPadding.toPx() -
                lineDataList[i - 1].value * yStep

        val x1 = x0 + (x - x0) * controlFactor
        val y1 = y0

        val x2 = x - (x - x0) * controlFactor
        val y2 = y

        path.cubicTo(x1, y1, x2, y2, x, y)
        if (lineChartOptions.drawLineVaues) drawLineValue(
            x.toInt(),
            lineDataList[i].value.toInt(),
            yStep,
            lineChartOptions = LineChartOptions()
        )
        drawCircle(color = lineChartOptions.lineColor, radius = 8f, center = Offset(x, y))
    }

    drawPath(
        path = path,
        color = lineChartOptions.lineColor,
        style = Stroke(width = lineChartOptions.lineStroke.value , cap = StrokeCap.Round)
    )


    drawXAxisLabels(lineDataList, xStep, lineChartOptions, lineChartOptions.textColor)

}




private fun DrawScope.drawYAxis(lineChartOptions: LineChartOptions) {
    // Draw the y-axis
    drawLine(
        start = Offset(lineChartOptions.internalPadding.toPx(), 0f),
        end = Offset(
            lineChartOptions.internalPadding.toPx(),
            size.height + lineChartOptions.internalPadding.toPx()
        ),
        color = Color.Gray,
        strokeWidth = 1f
    )


}

private fun DrawScope.drawYAxisLabel(
    yStep: Float,
    maxValue: Int,
    lineChartOptions: LineChartOptions
) {
    // Draw points on y-axis
    for (i in 0 until lineChartOptions.numOfYLabels+1) {
        val value = maxValue * i / lineChartOptions.numOfYLabels
        val y = size.height - lineChartOptions.internalPadding.toPx() - value * yStep
        val text = "${formatAmount(value.toDouble(), int = false)} ${lineChartOptions.unit}"

        val paint = android.graphics.Paint().apply {
            color = lineChartOptions.textColor.hashCode()
            textSize = lineChartOptions.textSize.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }
        drawContext.canvas.nativeCanvas.drawText(text, 0f, y - 8, paint)
        if (lineChartOptions.drawGuideLines) drawGuideLines(lineChartOptions, y)

    }

}

private fun DrawScope.drawGuideLines(lineChartOptions: LineChartOptions, y: Float) {

    drawLine(
        start = Offset(-lineChartOptions.internalPadding.toPx(), y),
        end = Offset(size.width + lineChartOptions.internalPadding.toPx(), y),
        color = Color(0f, 0f, 0f, 0.1f),
        strokeWidth = 1f
    )

}

private fun DrawScope.drawXAxis(
    barDataList: List<LineData>,
    xStep: Float,
    lineChartOptions: LineChartOptions
) {

    drawLine(
        start = Offset(-47f, size.height - lineChartOptions.internalPadding.toPx()),
        end = Offset(
            size.width + lineChartOptions.internalPadding.toPx(),
            size.height - lineChartOptions.internalPadding.toPx()
        ),
        color = Color.Gray,
        strokeWidth = 2f
    )

}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
private fun DrawScope.drawBars(
    barDataList: List<BarData>,
    xStep: Float,
    yStep: Float,
    barChartOptions: BarChartOptions
) {
    for ((i, barData) in barDataList.withIndex()) {
        val x = (i * xStep) + barChartOptions.barWidth.toPx() / 2
        val barWidth = barChartOptions.barWidth.toPx()
        val barHeight = barData.value * yStep
        drawRoundRect(
            color = barChartOptions.barColor,
            topLeft = Offset(
                x,
                size.height - barChartOptions.internalPadding.toPx() - barHeight
            ),
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(12f, 14f)
        )
        if (barChartOptions.drawBarValues) drawLineValue(
            x.toInt(),
            barData.value,
            yStep,
            lineChartOptions = LineChartOptions()
        )
    }
}

fun DrawScope.drawLineValue(
    x: Int,
    value: Int,
    yStep: Float,
    lineChartOptions: LineChartOptions
) {
    val y = size.height - lineChartOptions.internalPadding.toPx() - value * yStep
    val text = " ${formatAmount(value.toDouble(), false)}"
    val xnew = x.toFloat() + 4.dp.toPx()
    val ynew = y - 8.dp.toPx()
    val paint = Paint().apply {
        color = lineChartOptions.textColor.hashCode()
        textSize = lineChartOptions.textSize.toPx()
        textAlign = Paint.Align.LEFT
    }
    drawContext.canvas.nativeCanvas.drawText(text, xnew, ynew, paint)
}

private fun DrawScope.drawXAxisLabels(
    barDataList: List<LineData>,
    xStep: Float,
    barChartOptions: LineChartOptions,
    textColor: Color
) {
    val paint = android.graphics.Paint().apply {
        color = barChartOptions.textColor.hashCode()
        textSize = barChartOptions.textSize.toPx()
        textAlign = android.graphics.Paint.Align.CENTER
    }
    for ((i, barData) in barDataList.withIndex()) {
        val x = (i * xStep)
        val label = barData.label
        val xLabel = x
        val yLabel = size.height - barChartOptions.internalPadding.toPx() + 16.dp.toPx()
        drawContext.canvas.nativeCanvas.drawText(label, xLabel, yLabel, paint)
    }
}

data class LineData(
    val label: String, // Label for the data series
    val value: Float, // List of data points for the line
)

@androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
data class LineChartOptions(
    val drawXAxis: Boolean = true, // Whether to draw the X-axis
    val drawYAxis: Boolean = true, // Whether to draw the Y-axis
    val drawYLables: Boolean = true, // draw the labels for y axis
    val internalPadding: Dp = 16.dp, // Padding inside the chart
    val textSize: TextUnit = 12.sp, // Text size for labels
    val font: FontFamily? = null, // Custom font (optional)
    val fontStyle: FontStyle = FontStyle(
        FontStyle.FONT_WEIGHT_MEDIUM,
        FontStyle.FONT_SLANT_UPRIGHT
    ),
    val unit: String = "", // Unit for Y-axis labels
    val textColor: Color = Color.Gray, // Text color
    val drawGuideLines: Boolean = true, // Whether to draw horizontal guide lines
    val drawPoints: Boolean = true, // Whether to draw points at data values
    val pointRadius: Dp = 4.dp, // Radius of data points
    val lineColor: Color = Color.Blue, // Line color
    val lineStroke: Dp = 2.dp,
    val lineWidth: Dp = 10.dp,
    val lineSpacing: Dp = lineWidth / 2,// Width of the line
    val numOfYLabels : Int = 5,
    val drawLineVaues : Boolean = false
)


@Composable
fun SmoothLineChart(
    values: List<PointF>,
    modifier: Modifier = Modifier,
    smoothness: Float = 0.3f,
    chartColor: Color = Color(0xFF0099CC),
    circleSize: Float = 8f,
    strokeWidth: Float = 2f,
    xAxisLabels: List<String> = emptyList(),
    yAxisLabels: List<String> = emptyList(),
) {
    var maxY by remember { mutableStateOf(values.firstOrNull()?.y ?: 0f) }
    var minY by remember { mutableStateOf(values.firstOrNull()?.y ?: 0f) }

    if (values.isNotEmpty()) {
        for (point in values) {
            val y = point.y
            maxY = max(maxY, y)
            minY = min(minY, y)
        }
    }

    val chartPadding = 16.dp
    val axisTextSize = 12.sp
    val axisLabelPadding = 4.dp
    val paint = Paint().apply {
        color = color.blue
        textSize = 12f
        textAlign = Paint.Align.CENTER
    }
    val axisLabelTextStyle = TextStyle(
        fontSize = axisTextSize,
        fontWeight = FontWeight.Bold,
        color = Color.Gray
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val height = size.height - 2 * circleSize
            val width = size.width - 2 * circleSize
            val left = values.firstOrNull()?.x ?: 0f
            val right = values.lastOrNull()?.x ?: 0f
            val dX = max(right - left, 2f)
            val dY = max(maxY - minY, 2f)

            val path = Path()

            val points = mutableListOf<PointF>()
            for (point in values) {
                val x = circleSize + (point.x - left) * width / dX
                val y = circleSize + height - (point.y - minY) * height / dY
                points.add(PointF(x, y))
            }

            var lX = 0f
            var lY = 0f
            path.moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val p = points[i]
                val p0 = points[i - 1]
                val d0 = sqrt((p.x - p0.x).pow(2) + (p.y - p0.y).pow(2))
                val x1 = min(p0.x + lX * d0, (p0.x + p.x) / 2)
                val y1 = p0.y + lY * d0

                val p1 = points.getOrElse(i + 1) { points[i] }
                val d1 = sqrt((p1.x - p0.x).pow(2) + (p1.y - p0.y).pow(2))
                lX = (p1.x - p0.x) / d1 * smoothness
                lY = (p1.y - p0.y) / d1 * smoothness
                val x2 = max(p.x - lX * d0, (p0.x + p.x) / 2)
                val y2 = p.y - lY * d0

                path.cubicTo(x1, y1, x2, y2, p.x, p.y)
            }

            drawPath(
                path = path,
                color = chartColor,
                style = Stroke(width = strokeWidth),
            )

            if (values.size > 0) {
                path.lineTo(points.last().x, height + circleSize)
                path.lineTo(points.first().x, height + circleSize)
                path.close()

                drawPath(
                    path = path,
                    color = chartColor.copy(alpha = 0.2f),
                    style = Fill,
                )
            }

            for (point in points) {
                drawCircle(
                    color = chartColor,
                    radius = circleSize / 2,
                    center = Offset(point.x, point.y),
                )
                drawCircle(
                    color = Color.White,
                    radius = (circleSize - strokeWidth) / 2,
                    center = Offset(point.x, point.y),
                )
            }


        }
    }
}


@Preview
@Composable
fun SmoothLineChartPreview() {
    val values = listOf(
        PointF(0f, 10f),
        PointF(1f, 20f),
        PointF(2f, 30f),
        PointF(3f, 15f),
        PointF(4f, 25f)
    )

    SmoothLineChart(values = values)
}
