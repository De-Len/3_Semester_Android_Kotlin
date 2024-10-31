package com.example.mydialer

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.search.SearchBar
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.BuildConfig
import timber.log.Timber
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var contactsList: MutableList<Contact> = mutableListOf()
    lateinit var originalContactsList: List<Contact>
    private lateinit var recyclerView: RecyclerView
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

        Timber.plant(Timber.DebugTree())

        recyclerView = findViewById(R.id.rView)
        val searchButton: Button = findViewById(R.id.btn_search)
        val searchField: EditText = findViewById(R.id.et_search)


        val client = OkHttpClient()
        val url = "https://drive.google.com/u/0/uc?id=1-KO-9GA3NzSgIc1dkAsNm8Dqw0fuPxcR&export=download"
        val request = Request.Builder().url(url).build()


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                val json = response.body?.string()
                Timber.d(json ?: "Получен пустой результат")

                if (json != null) {
                    val wrapper = Gson().fromJson(json, Array<Contact>::class.java).toList()
                    contactsList.addAll(wrapper)

                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (e: IOException) {
                Timber.e("Ошибка запроса: ${e.message}")
            }
        }
        adapter = Adapter(this, contactsList) {}
        recyclerView.adapter = adapter

        originalContactsList = contactsList
        setOnClickListenerSearch(searchButton, searchField)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setOnClickListenerSearch(searchButton: Button, searchField: EditText) {
        searchButton.setOnClickListener {
            val searchResult: MutableList<Contact> = mutableListOf()
            val query = searchField.text.toString()
            if (query == "") {
                adapter.updateData(originalContactsList)
                return@setOnClickListener
            }
            contactsList.forEach {contact ->
                val name = contact.name
                val type = contact.type

                if (name.contains(query, ignoreCase = true) || type.contains(query, ignoreCase = true)) {
                    searchResult.add(contact)
                }
            }
            adapter.updateData(searchResult)
        }
    }
}

data class Contact(
    val name: String, val phone: String, val type: String
)

class Adapter(
    private val context: Context,
    private var list: List<Contact>,
    private val cellClickListener: (String) -> Unit
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textName: TextView = view.findViewById(R.id.rTextView_view_name)
        val textPhone: TextView = view.findViewById(R.id.rTextView_view_phone)
        val textType: TextView = view.findViewById(R.id.rTextView_view_type)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rview_item, parent, false)
        view.setOnClickListener {
//            val imageView = view.findViewById<ImageView>(R.id.rImageView_view)
//            cellClickListener(imageView.contentDescription.toString())
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.textName.text = data.name
        holder.textPhone.text = data.phone
        holder.textType.text = data.type
    }
    fun updateData(newList: List<Contact>) {
        this.list = newList
        notifyDataSetChanged()
    }
}