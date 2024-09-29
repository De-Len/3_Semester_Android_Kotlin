package com.example.complexevent

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        var progressCounter = 0

        val buttonSave: Button = findViewById(R.id.button_save)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val checkBox: CheckBox = findViewById(R.id.checkBox)
        val editText: EditText = findViewById(R.id.editText)
        val textView: TextView = findViewById(R.id.textView)

        buttonSave.setOnClickListener {
            if (checkBox.isChecked) {
                textView.text = editText.text
                progressCounter += 10
                progressBar.progress = progressCounter

                editText.backgroundTintList = ColorStateList.valueOf(Color.BLUE)
                Thread {
                    Thread.sleep(2000)
                    editText.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
                }.start()
            }
        }

    }
}