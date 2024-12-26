package com.example.weatherapp

import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: Adapter
    private lateinit var mService: RetrofitServices
    private val FAVORITES_REQUEST_CODE = 1

    private val PREFS_NAME = "myPreferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val rView: RecyclerView = findViewById(R.id.rView)
        adapter = Adapter(DiffCallback())
        rView.adapter = adapter
        rView.layoutManager = LinearLayoutManager(this)

        mService = Common.retrofitService

        val etSearch: EditText = findViewById(R.id.et_search)
        val btnSearch: Button = findViewById(R.id.btn_search)
        val btnSaveCity: ImageView = findViewById(R.id.save_city_icon)
        val openButton: Button = findViewById(R.id.open_favourites)
        val cityNameTextView: TextView = findViewById(R.id.cityName)

        btnSearch.setOnClickListener {
            val cityNameInSearch = etSearch.text.toString().trim()
            if (cityNameInSearch.isNotEmpty()) {
                fetchWeather(cityNameInSearch)
            }
        }

        btnSaveCity.setOnClickListener {
            val cityNameInSearch = etSearch.text.toString().trim()
            if (cityNameInSearch.isNotEmpty()) {
                saveFavoriteCity(cityNameInSearch)
            }
        }

        openButton.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivityForResult(intent, FAVORITES_REQUEST_CODE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FAVORITES_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getStringExtra("cityName")?.let { cityName ->
                val etSearch: EditText = findViewById(R.id.et_search)
                etSearch.setText(cityName)
                fetchWeather(cityName)
            }
        }
    }

    private fun fetchWeather(cityName: String) {
        val cityNameTextView: TextView = findViewById(R.id.cityName)
        cityNameTextView.text = "Название города: " + cityName
        val appid = BuildConfig.OPEN_WEATHER_API_KEY
        mService.getWeatherList(cityName, appid).enqueue(object : Callback<WeatherForecast> {
            override fun onResponse(call: Call<WeatherForecast>, response: Response<WeatherForecast>) {
                if (response.isSuccessful) {
                    val forecast = response.body()
                    adapter.submitList(forecast?.list)
                }
            }

            override fun onFailure(call: Call<WeatherForecast>, t: Throwable) {
            }
        })
    }

    private fun saveFavoriteCity(cityName: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val existingCities = sharedPreferences.getStringSet("favoriteCities", mutableSetOf()) ?: mutableSetOf()

        existingCities.add(cityName)

        editor.putStringSet("favoriteCities", existingCities)
        editor.apply()
    }
}