

# Graphosable - UI Chart Library

It is a versatile UI library for Android that provides easy-to-use and customizable Pie Charts, Line Charts, and Bar Charts. It simplifies the process of integrating interactive and visually appealing charts into your Android applications.

## Features

- **Pie Charts:** Create informative pie charts to represent data distribution in a visually appealing way.
- **Line Charts:** Visualize trends and data over time with interactive line charts.
- **Bar Charts:** Display data comparisons using bar charts with flexible customization options.

## Installation

To use [Library Name] in your Android project, follow these simple steps:

1. Open your project's `build.gradle` file.

2. Add the following dependency to your app module:

   ```gradle
   dependencies {
	        implementation 'com.github.dvghule121:Graphosable:v1.1.4'
	}
   ```

3. Sync your project with Gradle to ensure the library is downloaded and added to your project.

## Usage

### Pie Chart

``` Jetpack Compose
Graphs().PieChartWithLabels(data = listOf(Slice(9000),Slice(900), Slice(900), Slice(400),Slice(2200)), textColor = Color.LightGray.hashCode(), ringSize = 100f, context = this@MainActivity)
                  
```



### Line Chart

```Jetpack Compose
 LineChart(
  lineDataList = list,
  modifier = Modifier,
  lineChartOptions = LineChartOptions(lineWidth = 50.dp, lineColor = Color.Green, lineStroke = 8.dp, numOfYLabels = 2),

                    )
```



### Bar Chart

```Jetpack
 BarChart(
barDataList = list,
modifier = Modifier,
barChartOptions = BarChartOptions( barWidth = 25.dp,barSpacing = 15.dp,  textColor = Color.LightGray, drawYAxis = false, drawBarValues = false, drawXAxis = false)                        )
```



## Customization

Graphosable offers a wide range of customization options for each chart type. You can customize colors, labels, and more to match your app's design and requirements. Refer to the documentation for detailed customization instructions.


## License

This library is released under the [MIT License](LICENSE.md). You are free to use, modify, and distribute it as per the terms of the license.

## Contact

If you have any questions or need support, feel free to contact us at dynocodes@gmail.com .
