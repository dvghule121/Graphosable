package com.dynocodes.graphosable


import android.graphics.Paint
import android.graphics.fonts.FontFamily
import android.graphics.fonts.FontStyle
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.log10
import kotlin.math.pow


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun BarChart(
    barDataList: List<BarData>,
    modifier: Modifier,
    barChartOptions: BarChartOptions = BarChartOptions(),
    textColor: Color = Color.White
) {
    var maxValue = barDataList.map { it.value }.maxOrNull() ?: 0
    maxValue = calculateMagnitude(maxValue)

    Row(modifier = modifier
        .fillMaxSize()
        .padding(top = 24.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    barChartOptions.internalPadding + barChartOptions.internalPadding,
                    0.dp,
                    barChartOptions.internalPadding,
                    barChartOptions.internalPadding
                )
        ) {

            val yStep = (size.height - barChartOptions.internalPadding.toPx() * 2) / maxValue
            if (barChartOptions.drawYLables) drawYAxisLabel(yStep, maxValue, barChartOptions)
            if (barChartOptions.drawYAxis) drawYAxis(yStep, maxValue, barChartOptions)

        }


        Canvas(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .requiredWidth(((barChartOptions.barWidth + barChartOptions.barSpacing) * barDataList.size) + 10.dp)
                .fillMaxHeight()
                .padding(0.dp, barChartOptions.internalPadding)
        ) {
            val xStep = barChartOptions.barSpacing.toPx() + barChartOptions.barWidth.toPx()
            val yStep = (size.height - barChartOptions.internalPadding.toPx() * 2) / maxValue

            if (barChartOptions.drawXAxis) drawXAxis(barDataList, xStep, barChartOptions)

            drawXAxisLabels(barDataList, xStep, barChartOptions, textColor)
            drawBars(barDataList, xStep, yStep, barChartOptions)
        }
    }
}

private fun DrawScope.drawYAxis(yStep: Float, maxValue: Int, barChartOptions: BarChartOptions) {
    // Draw the y-axis
    drawLine(
        start = Offset(barChartOptions.internalPadding.toPx(), 0f),
        end = Offset(
            barChartOptions.internalPadding.toPx(),
            size.height + barChartOptions.internalPadding.toPx()
        ),
        color = Color.Gray,
        strokeWidth = 1f
    )


}

private fun DrawScope.drawYAxisLabel(
    yStep: Float,
    maxValue: Int,
    barChartOptions: BarChartOptions
) {
    // Draw points on y-axis
    for (i in 0..4) {
        val value = maxValue * i / 4
        val y = size.height - barChartOptions.internalPadding.toPx() - value * yStep
        val text = "${formatAmount(value.toDouble(), int = false)} ${barChartOptions.unit}"

        val paint = Paint().apply {
            color = barChartOptions.textColor.hashCode()
            textSize = barChartOptions.textSize.toPx()
            textAlign = Paint.Align.CENTER
        }
        drawContext.canvas.nativeCanvas.drawText(text, 0f, y - 8, paint)
        if (barChartOptions.drawGuideLines) drawGuideLines(barChartOptions, y)

    }

}

private fun DrawScope.drawGuideLines(barChartOptions: BarChartOptions, y: Float) {

    drawLine(
        start = Offset(-barChartOptions.internalPadding.toPx(), y),
        end = Offset(size.width + barChartOptions.internalPadding.toPx(), y),
        color = Color(0f, 0f, 0f, 0.1f),
        strokeWidth = 1f
    )

}

private fun DrawScope.drawXAxis(
    barDataList: List<BarData>,
    xStep: Float,
    barChartOptions: BarChartOptions
) {

    drawLine(
        start = Offset(0f, size.height - barChartOptions.internalPadding.toPx()),
        end = Offset(
            size.width + barChartOptions.internalPadding.toPx(),
            size.height - barChartOptions.internalPadding.toPx()
        ),
        color = Color.Gray,
        strokeWidth = 2f
    )

}

private fun DrawScope.drawBars(
    barDataList: List<BarData>,
    xStep: Float,
    yStep: Float,
    barChartOptions: BarChartOptions
) {
    for ((i, barData) in barDataList.withIndex()) {
        val x = (i * xStep) + barChartOptions.barWidth.toPx() / 2
        val barWidth = barChartOptions.barWidth.toPx()
        val barHeight =( barData.value * yStep)
        drawRoundRect(
            color = barChartOptions.barColor,
            topLeft = Offset(
                x,
                size.height - barChartOptions.internalPadding.toPx() - barHeight
            ),
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(12f, 14f)
        )
        if (barChartOptions.drawBarValues) drawBarValue(
            x.toInt(),
            barData.value,
            yStep,
            barChartOptions
        )
    }
}

fun DrawScope.drawBarValue(
    x: Int,
    value: Int,
    yStep: Float,
    barChartOptions: BarChartOptions
) {
    val y = size.height - barChartOptions.internalPadding.toPx() - value * yStep
    val text = " ${formatAmount(value.toDouble(), false)}"
    val xnew = x.toFloat() + 4.dp.toPx()
    val ynew = y - 8.dp.toPx()
    val paint = Paint().apply {
        color = barChartOptions.textColor.hashCode()
        textSize = barChartOptions.textSize.toPx()
        textAlign = Paint.Align.LEFT
    }
    drawContext.canvas.nativeCanvas.drawText(text, xnew, ynew, paint)
}

fun calculateMagnitude(value: Int): Int {
    if (value <= 0) return 1000 // Handle non-positive values

    val magnitude = 10.0.pow(log10(value.toDouble()).toInt())
    val nextMagnitude = magnitude * 10

    return if (value <= magnitude * 5) magnitude.toInt() * 5 else nextMagnitude.toInt()
}


private fun DrawScope.drawXAxisLabels(
    barDataList: List<BarData>,
    xStep: Float,
    barChartOptions: BarChartOptions,
    textColor: Color
) {
    val paint = Paint().apply {
        color = barChartOptions.textColor.hashCode()
        textSize = barChartOptions.textSize.toPx()
        textAlign = Paint.Align.CENTER
    }
    for ((i, barData) in barDataList.withIndex()) {
        val x = (i * xStep) + barChartOptions.barWidth.toPx()
        val label = barData.label
        val xLabel = x
        val yLabel = size.height - barChartOptions.internalPadding.toPx() + 16.dp.toPx()
        drawContext.canvas.nativeCanvas.drawText(label, xLabel, yLabel, paint)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
data class BarChartOptions(
    val barWidth: Dp = 25.dp,
    val drawXAxis: Boolean = true,
    val drawYAxis: Boolean = true,
    val internalPadding: Dp = 16.dp,
    val textSize: TextUnit = 12.sp,
    val font: FontFamily? = null, // Specify your custom font
    val fontStyle: FontStyle = FontStyle(
        FontStyle.FONT_WEIGHT_MEDIUM,
        FontStyle.FONT_SLANT_UPRIGHT
    ),
    val barColor: Color = Color.LightGray,
    val unit: String = " ",
    val textColor: Color = Color.LightGray,
    val drawGuideLines: Boolean = true,
    val drawBarValues: Boolean = true,
    val drawYLables: Boolean = true,
    val drawXLables: Boolean = true,
    val barSpacing: Dp = barWidth / 2
)