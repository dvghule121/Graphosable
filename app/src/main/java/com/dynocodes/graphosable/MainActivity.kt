package com.dynocodes.graphosable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dynocodes.graphosable.ui.theme.GraphosableTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GraphosableTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()


                    ) {
//                    val list = ArrayList<BarData>()
//                    for (i in 1..7) {
//                        list.add(BarData(i.toFloat(), "$i"))
//                    }
//                    Graphs().PieChartWithLabels(
//                        modifier = Modifier.fillMaxWidth()
//                            .height(350.dp),
//                        data = listOf(
//                            Slice(120, "Steel"),
//                            Slice(90, "Steel"),
//                            Slice(40, "Steel")
//                        ), context = this, ringSize = 50f
//                    )

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


