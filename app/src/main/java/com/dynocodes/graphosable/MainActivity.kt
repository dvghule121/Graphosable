package com.dynocodes.graphosable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dynocodes.graphosable.ui.theme.GraphosableTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GraphosableTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)


                ) {



                    val list = ArrayList<BarData>()
                    for (i in listOf(1,10,100,500,800,300,500,700,800,100,500,500,300)) {
                        list.add(BarData( i ,"$i"))
                    }
//                    SmoothLineChart(values = list)
//
//                    LineChart(
//                        lineDataList = list,
//                        modifier = Modifier,
//                        lineChartOptions = LineChartOptions(lineWidth = 50.dp, lineColor = Color.Green, lineStroke = 8.dp, numOfYLabels = 2),
//
//                    )

                    BarChart(
                        barDataList = list, modifier = Modifier, barChartOptions = BarChartOptions(
                            barWidth = 25.dp, barSpacing = 15.dp,  textColor = Color.LightGray, drawYAxis = false, drawBarValues = false, drawXAxis = false)
                        )

//                    Graphs().LineChart(
//                        Modifier,
//                        listOf(20f, 50f, 80f, 70f, 85f, 46f, 35f, 30f),
//                        listOf("a", "b", "x", "d", "g", "e", "f", "v"),
//                        ""
//                    )
//                        Graphs().PieChartWithLabels(data = listOf(Slice(9000),Slice(900), Slice(900), Slice(400),Slice(2200)), textColor = Color.LightGray.hashCode(), ringSize = 100f, context = this@MainActivity)
//                        Graphs().BarChart(barDataList = list, modifier = Modifier, textColor = Color.LightGray.hashCode())


                }
            }
        }
    }
}

fun generateParabolicRange(
    start: Float,
    end: Float,
    numPoints: Int
): List<Float> {
    require(numPoints >= 2) { "Number of points must be at least 2." }

    val range = mutableListOf<Float>()

    for (i in 0 until numPoints) {
        val t = i.toFloat() / (numPoints - 1)
        val value = start + (end - start) * t * t
        range.add(value)
    }

    return range
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


