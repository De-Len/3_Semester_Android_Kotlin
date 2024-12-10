package com.example.mydialer
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mydialer.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private var contactsList: MutableList<Contact> = mutableListOf()
    lateinit var originalContactsList: List<Contact>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter

    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences: SharedPreferences
    private val searchFilterKey = "SEARCH_FILTER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val savedFilter = sharedPreferences.getString(searchFilterKey, "")
        binding.etSearch.setText(savedFilter) // Устанавливаем сохранённое значение в EditText


        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var searchResult: MutableList<Contact> = mutableListOf()

        Timber.plant(Timber.DebugTree())

        recyclerView = binding.rView

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
        adapter = Adapter {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:${it}"))
            this.startActivity(intent)
        }
        recyclerView.adapter = adapter

        originalContactsList = contactsList

        // Прослушиватель
        binding.etSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(query: Editable) {
                sharedPreferences.edit().putString(searchFilterKey, query.toString()).apply()
            }

            override fun beforeTextChanged(query: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(query: CharSequence, start: Int,
                                       before: Int, count: Int) {
                searchResult = mutableListOf()
                if (query == "") {
                    adapter.submitList(originalContactsList)
                    return
                }
                contactsList.forEach {contact ->
                    val name = contact.name
                    val type = contact.type

                    if (name.contains(query, ignoreCase = true) || type.contains(query, ignoreCase = true)) {
                        searchResult.add(contact)
                    }
                }
                adapter.submitList(searchResult)
            }
        })


    }
}

data class Contact(
    val name: String, val phone: String, val type: String
)

class Adapter(
    private val cellClickListener: (String) -> Unit
) : ListAdapter<Contact, Adapter.ViewHolder>(ContactDiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textName: TextView = view.findViewById(R.id.rTextView_view_name)
        val textPhone: TextView = view.findViewById(R.id.rTextView_view_phone)
        val textType: TextView = view.findViewById(R.id.rTextView_view_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position) // Получаем элемент через getItem
        holder.textName.text = data.name
        holder.textPhone.text = data.phone
        holder.textType.text = data.type

        holder.itemView.setOnClickListener {
            cellClickListener(data.phone)
        }
    }
}

class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.phone == newItem.phone // Уникально по телефону
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem // Сравнение содержимого
    }
}