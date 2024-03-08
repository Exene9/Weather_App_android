package edu.fz.cs411.weatherapp
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.isNotEmpty
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class MainActivity : AppCompatActivity() {

    private lateinit var tvCityName: TextView
    private lateinit var tvFeelsLikeTemperature: TextView
    private lateinit var tvMinMaxTemp: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWindSpeed: TextView
    private lateinit var tvSunriseTime: TextView
    private lateinit var tvSunsetTime: TextView
    private lateinit var saveButton: Button
    private lateinit var loadButton: Button
    private val STATE_CITY_NAME = "state_city_name"
    private val STATE_FEELS_LIKE_TEMP = "state_feels_like_temp"
    private val STATE_MIN_MAX_TEMP = "state_min_max_temp"
    private val STATE_HUMIDITY = "state_humidity"
    private val STATE_WIND_SPEED = "state_wind_speed"
    private val STATE_SUNRISE_TIME = "state_sunrise_time"
    private val STATE_SUNSET_TIME = "state_sunset_time"
    private val STATE_LANGUAGE_PREFERENCE = "state_language_preference"
    private lateinit var dataSource: DataSource
    private var recyclerViewState: Parcelable? = null
    private lateinit var getCity: EditText
    private lateinit var search: Button
    private lateinit var viewModel: WeatherViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private val LAST_ENTERED_CITY_KEY = "last_entered_city"
    private val dataStore by lazy {
        createDataStore(name = "app_preferences")
    }
    private var isIntroVisible = true
    private var isTableVisible = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isIntroVisible", isIntroVisible)
        outState.putBoolean("isTableVisible", isTableVisible)
        outState.putString(STATE_CITY_NAME, tvCityName.text.toString())
        outState.putString(STATE_FEELS_LIKE_TEMP, tvFeelsLikeTemperature.text.toString())
        outState.putString(STATE_MIN_MAX_TEMP, tvMinMaxTemp.text.toString())
        outState.putString(STATE_HUMIDITY, tvHumidity.text.toString())
        outState.putString(STATE_WIND_SPEED, tvWindSpeed.text.toString())
        outState.putString(STATE_SUNRISE_TIME, tvSunriseTime.text.toString())
        outState.putString(STATE_SUNSET_TIME, tvSunsetTime.text.toString())

    }
    private suspend fun loadLanguagePreferenceFromDataStore(): String {
        val data = dataStore.data.first()
        return data[preferencesKey<String>(STATE_LANGUAGE_PREFERENCE)] ?: ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        // Initialize the SharedPreferences
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        // Initialize the DataSource
        dataSource = DataSource(this)
        val languagePreference = runBlocking {
            loadLanguagePreferenceFromDataStore()
        }


        applyLanguagePreference(languagePreference)

        // Use the main_activity_layout by default
        setContentView(R.layout.activity_main)
        val changeLanguageButton = findViewById<Button>(R.id.langBut)

        changeLanguageButton.setOnClickListener {}
        // Link Views
        getCity = findViewById(R.id.getCity)
        search = findViewById(R.id.button)
        tvCityName = findViewById(R.id.tvCityName)
        tvFeelsLikeTemperature = findViewById(R.id.tvFeelsLikeTemperature)
        tvMinMaxTemp = findViewById(R.id.tvMinMaxTemp)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvWindSpeed = findViewById(R.id.tvWindSpeed)
        tvSunriseTime = findViewById(R.id.tvSunriseTime)
        tvSunsetTime = findViewById(R.id.tvSunsetTime)
        saveButton=findViewById(R.id.save)
        loadButton=findViewById(R.id.buttonLoad)
        // Set an OnClickListener for the search button
        search.setOnClickListener {
            // Get the entered city name from the EditText and update the ViewModel
            val city = getCity.text.toString()
            viewModel.setCityName(city)

            // Save the entered city to SharedPreferences
            sharedPreferences.edit().putString(LAST_ENTERED_CITY_KEY, city).apply()

            val apiKey = "b3e8900ace0cd61dd09a3524f646bac7"

            lifecycleScope.launch {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.webservice.getCurrentWeather(city.toString(), apiKey, "metric")
                }

                // Check if the response is successful
                if (response.isSuccessful) {
                    isIntroVisible = false
                    Log.d("APIresponse", "Successful")
                    val weatherData = response.body()
                    weatherData?.let { data ->
                        findViewById<View>(R.id.introLayout).visibility = View.GONE
                        findViewById<View>(R.id.acLayout).visibility = View.VISIBLE

                        tvCityName.text = city
                        tvFeelsLikeTemperature.text = getString(R.string.feels_like_temperature, data.main.feels_like)
                        tvMinMaxTemp.text = getString(R.string.min_max_temperature, data.main.temp_min, data.main.temp_max)
                        tvHumidity.text = getString(R.string.humidity, data.main.humidity)
                        tvWindSpeed.text = getString(R.string.wind_speed, data.wind.speed)
                        tvSunriseTime.text = getString(R.string.sunrise_time, convertTime(data.sys.sunrise))
                        tvSunsetTime.text = getString(R.string.sunset_time, convertTime(data.sys.sunset))
                    saveButton.setOnClickListener{
                        val weatherModel = YourTableModel(
                            cityName = city,
                            feelsLikeTemperature = data.main.feels_like,
                            minTemperature = data.main.temp_min,
                            maxTemperature = data.main.temp_max,
                            humidity = data.main.humidity,
                            windSpeed = data.wind.speed,
                            sunriseTime = convertTime(data.sys.sunrise),
                            sunsetTime = convertTime(data.sys.sunset)
                        )
                        dataSource.saveData(weatherModel)
                        val duration = Toast.LENGTH_LONG
                        val toast1 = Toast.makeText(this@MainActivity, R.string.correct, duration)
                        toast1.show()
                        Log.d("SQL", "Successfuly Save")
                        val savedData = dataSource.getAllData()
                        for (data in savedData) {
                            Log.d("SavedData", "City: ${data.cityName}, Feels Like: ${data.feelsLikeTemperature}")
                        }



                    }

                    }
                } else {
                    Log.d("APIresponse", "Did not reply")
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this@MainActivity, R.string.invalid, duration)
                    toast.show()

                }
            }
        }

        loadButton.setOnClickListener {
            dataSource.saveData(YourTableModel())
            val savedDataList = dataSource.getAllData()

            if (savedDataList.isNotEmpty()) {
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = ListRecyclerAdapter(savedDataList)
                findViewById<View>(R.id.recyclerView).visibility = View.VISIBLE
                findViewById<View>(R.id.introLayout).visibility = View.GONE
                isTableVisible = true
                isIntroVisible = false
            } else {

                Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show()
            }
        }


        //check for background position
        if (savedInstanceState != null) {
            isIntroVisible = savedInstanceState.getBoolean("isIntroVisible", true)
            isTableVisible= savedInstanceState.getBoolean("isTableVisible", false)
            if (isIntroVisible) {
                findViewById<View>(R.id.introLayout).visibility = View.VISIBLE
                findViewById<View>(R.id.acLayout).visibility = View.GONE
                findViewById<View>(R.id.recyclerView).visibility = View.GONE
                Log.d("handling", "backOnDef")
            }
            else if (isTableVisible) {
                findViewById<View>(R.id.introLayout).visibility = View.GONE
                findViewById<View>(R.id.acLayout).visibility = View.GONE
                findViewById<View>(R.id.recyclerView).visibility = View.VISIBLE
                dataSource.saveData(YourTableModel())
                val savedDataList = dataSource.getAllData()
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = ListRecyclerAdapter(savedDataList)
              Log.d("handling", "backOnTable")

            } else {
                findViewById<View>(R.id.introLayout).visibility = View.GONE
                findViewById<View>(R.id.acLayout).visibility = View.VISIBLE
                findViewById<View>(R.id.recyclerView).visibility = View.GONE

                Log.d("handling", "backOnSearch")
            }

            // Restore text values
            tvCityName.text = savedInstanceState.getString(STATE_CITY_NAME, "")
            tvFeelsLikeTemperature.text = savedInstanceState.getString(STATE_FEELS_LIKE_TEMP, "")
            tvMinMaxTemp.text = savedInstanceState.getString(STATE_MIN_MAX_TEMP, "")
            tvHumidity.text = savedInstanceState.getString(STATE_HUMIDITY, "")
            tvWindSpeed.text = savedInstanceState.getString(STATE_WIND_SPEED, "")
            tvSunriseTime.text = savedInstanceState.getString(STATE_SUNRISE_TIME, "")
            tvSunsetTime.text = savedInstanceState.getString(STATE_SUNSET_TIME, "")
        }
        loadFromDataStore() // Load language preference from DataStore

        // Set an OnClickListener for the change language button
        changeLanguageButton.setOnClickListener {


            val languages = arrayOf(
                getString(R.string.eng),
                getString(R.string.esp),
                getString(R.string.por)
            )
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.lang)
            builder.setItems(languages) { _, which ->
                val selectedLanguage = when (which) {
                    0 -> "en" // English
                    1 -> "es" // Spanish
                    2 -> "pt"
                    else -> "en"// Default to English if unexpected selection
                }

                // Implement logic to change the language preference

                applyLanguagePreference(selectedLanguage.toString())

                // Save the updated language preference to DataStore
                runBlocking {
                    saveToDataStore(selectedLanguage.toString())
                }

                // Recreate the activity to apply language changes

                recreate()
            }

            builder.show()
        }



    }
    override fun onBackPressed() {
        if (isTableVisible) {
            // If RecyclerView is visible, hide it and show the introLayout
            findViewById<View>(R.id.recyclerView).visibility = View.GONE
            findViewById<View>(R.id.introLayout).visibility = View.VISIBLE
            isTableVisible = false
            isIntroVisible = true
        } else if (!isIntroVisible) {
            findViewById<View>(R.id.acLayout).visibility = View.GONE
            findViewById<View>(R.id.introLayout).visibility = View.VISIBLE
            isIntroVisible = true
        } else {

            super.onBackPressed()
        }
    }
    private fun loadFromDataStore() = runBlocking {
        val data = dataStore.data.first()

        tvCityName.setText(data[preferencesKey<String>(STATE_CITY_NAME)] ?: "")
        tvFeelsLikeTemperature.setText(data[preferencesKey<String>(STATE_FEELS_LIKE_TEMP)] ?: "")
        tvMinMaxTemp.setText(data[preferencesKey<String>(STATE_MIN_MAX_TEMP)] ?: "")
        tvHumidity.setText(data[preferencesKey<String>(STATE_HUMIDITY)] ?: "")
        tvWindSpeed.setText(data[preferencesKey<String>(STATE_WIND_SPEED)] ?: "")
        tvSunriseTime.setText(data[preferencesKey<String>(STATE_SUNRISE_TIME)] ?: "")
        tvSunsetTime.setText(data[preferencesKey<String>(STATE_SUNSET_TIME)] ?: "")
        val languagePreference = data[preferencesKey<String>(STATE_LANGUAGE_PREFERENCE)] ?: ""
        applyLanguagePreference(languagePreference)

        Log.d("loaded City", "$tvCityName")
    }

    private suspend fun saveToDataStore(languagePreference: String) {
        dataStore.edit { preferences ->
            preferences[preferencesKey(STATE_CITY_NAME)] = tvCityName.text.toString()
            preferences[preferencesKey(STATE_FEELS_LIKE_TEMP)] = tvFeelsLikeTemperature.text.toString()
            preferences[preferencesKey(STATE_MIN_MAX_TEMP)] = tvMinMaxTemp.text.toString()
            preferences[preferencesKey(STATE_HUMIDITY)] = tvHumidity.text.toString()
            preferences[preferencesKey(STATE_WIND_SPEED)] = tvWindSpeed.text.toString()
            preferences[preferencesKey(STATE_SUNRISE_TIME)] = tvSunriseTime.text.toString()
            preferences[preferencesKey(STATE_SUNSET_TIME)] = tvSunsetTime.text.toString()
            preferences[preferencesKey(STATE_LANGUAGE_PREFERENCE)] = languagePreference
        }

    }
    private fun applyLanguagePreference(languagePreference: String) {
        if (languagePreference.isNotEmpty()) {
            val newLocale = Locale(languagePreference)
            Locale.setDefault(newLocale)

            val configuration = resources.configuration
            configuration.setLocale(newLocale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        runBlocking {
            val languagePreference = runBlocking {
                loadLanguagePreferenceFromDataStore()
            }
            saveToDataStore( languagePreference)
        }
    }

    private fun convertTime(time: Long): String {
        val date = Date(time * 1000)
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(date)
    }
}
