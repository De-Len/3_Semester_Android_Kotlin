package com.example.recycleview

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val colorCustomList = listOf(
            ColorCustom("White", Color.parseColor("#FFFFFF")),
            ColorCustom("Black", Color.parseColor("#000000")),
            ColorCustom("Blue", Color.parseColor("#00BFFF")),
            ColorCustom("Red", Color.parseColor("#FF0000")),
            ColorCustom("Magenta", Color.parseColor("#FB00FF"))
        )

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.rView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = Adapter(this, colorCustomList) {
            onCellClickListener(it)
        }
    }
    fun onCellClickListener(color: String) {
        val toast: Toast = Toast.makeText(applicationContext, "IT'S $color", Toast.LENGTH_SHORT)
        toast.show()
    }
}


class Adapter(
    private val context: Context,
    private val list: List<ColorCustom>,
    private val cellClickListener: (String) -> Unit
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.rView_textView)
        val color: View = view.findViewById(R.id.rView_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rview_item, parent, false)
        view.setOnClickListener {
            val text = view.findViewById<TextView>(R.id.rView_textView)
            cellClickListener(text.text.toString())
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.title.text = data.colorName
        holder.color.setBackgroundColor(data.colorHex)
    }
}

data class ColorCustom(
    val colorName: String,
    val colorHex: Int
)

interface CellClickListener {
    fun onCellClickListener()
}

