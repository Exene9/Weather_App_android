package edu.fz.cs411.weatherapp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel : ViewModel() {
    val cityName = MutableLiveData<String>()
    val feelsLikeTemperature = MutableLiveData<String>()
    val minMaxTemp = MutableLiveData<String>()
    val humidity = MutableLiveData<String>()
    val windSpeed = MutableLiveData<String>()
    val sunriseTime = MutableLiveData<String>()
    val sunsetTime = MutableLiveData<String>()

    fun setCityName(value: String) {
        cityName.value = value
    }

    fun setFeelsLikeTemperature(value: String) {
        feelsLikeTemperature.value = value
    }

    fun setMinMaxTemp(value: String) {
        minMaxTemp.value = value
    }

    fun setHumidity(value: String) {
        humidity.value = value
    }

    fun setWindSpeed(value: String) {
        windSpeed.value = value
    }

    fun setSunriseTime(value: String) {
        sunriseTime.value = value
    }

    fun setSunsetTime(value: String) {
        sunsetTime.value = value
    }
}
