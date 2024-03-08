package edu.fz.cs411.weatherapp

data class YourTableModel(
    var cityName: String = "",
    var feelsLikeTemperature: Double = 0.0,
    var minTemperature: Double = 0.0,
    var maxTemperature: Double = 0.0,
    var humidity: Int = 0,
    var windSpeed: Double = 0.0,
    var sunriseTime: String = "",
    var sunsetTime: String = ""
)