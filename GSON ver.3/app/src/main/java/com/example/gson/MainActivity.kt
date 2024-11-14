package com.example.gson

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import okhttp3.*
import okio.IOException
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking



class MainActivity : AppCompatActivity() {
    private val linksList: MutableList<String> = mutableListOf()
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.rView)

        fetchData()

        adapter = Adapter(this, linksList) {
            onCellClickListener(it)
        }
        recyclerView.adapter = adapter
    }
    fun onCellClickListener(link: String) {
//        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        val clip: ClipData = ClipData.newPlainText("Ссылка на фото", link)
//        clipboard.setPrimaryClip(clip)
//        Timber.plant()
//        Timber.i(link)

        val picLink = link
        val intent = Intent(this, PicViewer::class.java)
        intent.putExtra("picLink", picLink)
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fetchData() {
        runBlocking {
            getResponse { response ->
                if (response != null) {
                    val wrapper = Gson().fromJson(response, Wrapper::class.java)
                    Timber.plant(Timber.DebugTree())

                    // Логируем каждые 5 фотографий
                    wrapper.photos.photo.forEachIndexed { index, photo ->
                        if ((index + 1) % 5 == 0) {
                            Timber.d(Gson().toJson(photo))
                        }
                    }
                    Timber.d(wrapper.toString())

                    // Добавляем ссылки на фотографии в linksList
                    wrapper.photos.photo.forEach { photo ->
                        val link: String = "https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}_z.jpg\n"
                        linksList.add(link)
                    }

                } else {
                    Timber.e("Не удалось получить ответ")
                }
                runOnUiThread {
                    adapter.notifyDataSetChanged()

                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageUrl = data?.getStringExtra("image_url")
            val imageFavorited = data?.getBooleanExtra("image_favorited", false)

            Timber.plant()
            Timber.d("РАБОТАЕТ")

            if (imageFavorited == true && imageUrl != null) {
                showSnackBar("Картинка добавлена в избранное", imageUrl)
            }
        }
    }

    private fun showSnackBar(message: String, imageUrl: String) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        snackBar.setAction("Открыть") {
            // Открытие изображения в браузере
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl))
            startActivity(browserIntent)
        }
        snackBar.show()
    }
}

data class Wrapper(
    val photos: PhotoPage
)

data class PhotoPage(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<Photo>
)

data class Photo(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val title: String,
    val ispublic: Int,
    val isfriend: Int,
    val isfamily: Int
)


fun getResponse(callback: (String?) -> Unit) {
    val url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Timber.plant()
            Timber.e("Flickr OkCats", "Ошибка подключения: ${e.message}")
            callback(null) // В случае ошибки, возвращаем null
        }

        override fun onResponse(call: Call, response: Response) {
            Timber.plant()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Timber.i("Flickr OkCats", "Ответ: $responseBody")
                callback(responseBody) // Возвращаем ответ через callback
            } else {
                Timber.e("Flickr OkCats", "Ошибка: ${response.code} ${response.message}")
                callback(null) // В случае ошибки, возвращаем null
            }
        }
    })
}


class Adapter(
    private val context: Context,
    private val list: List<String>,
    private val cellClickListener: (String) -> Unit
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.rImageView_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rview_item, parent, false)
        view.setOnClickListener {
            val imageView = view.findViewById<ImageView>(R.id.rImageView_view)
            cellClickListener(imageView.contentDescription.toString())
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        Glide.with(context)
            .load(data)
            .into(holder.image)
        holder.image.contentDescription = data
    }
}