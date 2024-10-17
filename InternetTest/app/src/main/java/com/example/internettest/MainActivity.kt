package com.example.internettest

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import okhttp3.*
import okio.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val url =
            "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1"

        val buttonHTTP = findViewById<Button>(R.id.btnHTTP)
        val buttonOkHTTP = findViewById<Button>(R.id.btnOkHTTP)

        buttonHTTP.setOnClickListener {
            val queue = Volley.newRequestQueue(this)
            val request = StringRequest(
                Request.Method.GET,
                url,
                { result ->
                    Log.d("Flickr cats", "Result: $result")
                },
                { error ->
                    Log.d("Flickr cats", "Error: $error")
                }
            )
            queue.add(request)
        }

        buttonOkHTTP.setOnClickListener {
            val client = OkHttpClient()

            // Убедитесь, что переменная url корректна
            val request = okhttp3.Request.Builder()
                .url(url)
                .build()

            // Выполнение асинхронного вызова
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Обработка ошибки
                    Log.e("Flickr OkCats", "Ошибка подключения: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        // Успешный ответ
                        val responseBody = response.body?.string()
                        Log.i("Flickr OkCats", "Ответ: $responseBody")
                    } else {
                        // Обработка неуспешных ответов
                        Log.e("Flickr OkCats", "Ошибка: ${response.code} ${response.message}")
                    }
                }
            })
        }
    }
}