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
import kotlin.random.Random


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
                    for (i in 1..7) {
                        list.add(BarData(i* (Random.nextInt(0, 10000)), "$i"))
                    }

                        Graphs().PieChartWithLabels(data = listOf(Slice(9000),Slice(900), Slice(900), Slice(400),Slice(2200)), textColor = Color.LightGray.hashCode(), ringSize = 100f, context = this@MainActivity)
//                        Graphs().BarChart(barDataList = list, modifier = Modifier, textColor = Color.LightGray.hashCode())





                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


