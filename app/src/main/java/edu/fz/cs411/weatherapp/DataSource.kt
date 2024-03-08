package edu.fz.cs411.weatherapp
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataSource(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)

    fun saveData(data: YourTableModel) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_CITY_NAME, data.cityName)
            put(COLUMN_FEELS_LIKE_TEMPERATURE, data.feelsLikeTemperature)
            put(COLUMN_MIN_TEMPERATURE, data.minTemperature)
            put(COLUMN_MAX_TEMPERATURE, data.maxTemperature)
            put(COLUMN_HUMIDITY, data.humidity)
            put(COLUMN_WIND_SPEED, data.windSpeed)
            put(COLUMN_SUNRISE_TIME, data.sunriseTime)
            put(COLUMN_SUNSET_TIME, data.sunsetTime)
        }

        db.insert(TABLE_NAME, null, values)

        db.close()
    }

    @SuppressLint("Range")
    fun getAllData(): List<YourTableModel> {
        val db = dbHelper.readableDatabase
        val data = mutableListOf<YourTableModel>()

        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        while (cursor.moveToNext()) {
            val minTemperature = cursor.getDouble(cursor.getColumnIndex(COLUMN_MIN_TEMPERATURE))
            val maxTemperature = cursor.getDouble(cursor.getColumnIndex(COLUMN_MAX_TEMPERATURE))

            // Check if both minTemperature and maxTemperature are not 0
            if (minTemperature != 0.0 && maxTemperature != 0.0) {
                val model = YourTableModel(
                    cityName = cursor.getString(cursor.getColumnIndex(COLUMN_CITY_NAME)),
                    feelsLikeTemperature = cursor.getDouble(cursor.getColumnIndex(COLUMN_FEELS_LIKE_TEMPERATURE)),
                    minTemperature = minTemperature,
                    maxTemperature = maxTemperature,
                    humidity = cursor.getInt(cursor.getColumnIndex(COLUMN_HUMIDITY)),
                    windSpeed = cursor.getDouble(cursor.getColumnIndex(COLUMN_WIND_SPEED)),
                    sunriseTime = cursor.getString(cursor.getColumnIndex(COLUMN_SUNRISE_TIME)),
                    sunsetTime = cursor.getString(cursor.getColumnIndex(COLUMN_SUNSET_TIME))
                )
                data.add(model)
            }
        }

        cursor.close()
        db.close()

        return data
    }


    fun deleteAllData() {
        val db = dbHelper.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }

    companion object {
        private const val TABLE_NAME = "your_table_name"
        private const val COLUMN_CITY_NAME = "city_name"
        private const val COLUMN_FEELS_LIKE_TEMPERATURE = "feels_like_temperature"
        private const val COLUMN_MIN_TEMPERATURE = "min_temperature"
        private const val COLUMN_MAX_TEMPERATURE = "max_temperature"
        private const val COLUMN_HUMIDITY = "humidity"
        private const val COLUMN_WIND_SPEED = "wind_speed"
        private const val COLUMN_SUNRISE_TIME = "sunrise_time"
        private const val COLUMN_SUNSET_TIME = "sunset_time"
    }
}

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "your_database_name.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "your_table_name"
        private const val COLUMN_CITY_NAME = "city_name"
        private const val COLUMN_FEELS_LIKE_TEMPERATURE = "feels_like_temperature"
        private const val COLUMN_MIN_TEMPERATURE = "min_temperature"
        private const val COLUMN_MAX_TEMPERATURE = "max_temperature"
        private const val COLUMN_HUMIDITY = "humidity"
        private const val COLUMN_WIND_SPEED = "wind_speed"
        private const val COLUMN_SUNRISE_TIME = "sunrise_time"
        private const val COLUMN_SUNSET_TIME = "sunset_time"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = (
                "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COLUMN_CITY_NAME TEXT," +
                        "$COLUMN_FEELS_LIKE_TEMPERATURE REAL," +
                        "$COLUMN_MIN_TEMPERATURE REAL," +
                        "$COLUMN_MAX_TEMPERATURE REAL," +
                        "$COLUMN_HUMIDITY INTEGER," +
                        "$COLUMN_WIND_SPEED REAL," +
                        "$COLUMN_SUNRISE_TIME TEXT," +
                        "$COLUMN_SUNSET_TIME TEXT)"
                )
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
}
