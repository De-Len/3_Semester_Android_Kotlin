package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class FavoritesActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var favoritesListView: ListView
    private lateinit var favorites: Set<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        favoritesListView = findViewById(R.id.favoritesListView)

        sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)

        favorites = sharedPreferences.getStringSet("favoriteCities", setOf())!!

        val favoritesList = favorites.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, favoritesList)
        favoritesListView.adapter = adapter

        favoritesListView.setOnItemClickListener { parent, view, position, id ->
            val selectedCity = favoritesList[position]
            val resultIntent = Intent()
            resultIntent.putExtra("cityName", selectedCity)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
