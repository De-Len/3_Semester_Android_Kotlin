package com.example.attributes

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.TypedValue

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

        val editText: EditText = findViewById(R.id.editText)

        val buttonBlackText: Button = findViewById(R.id.button_blackText)
        val buttonRedText: Button = findViewById(R.id.button_redText)
        val buttonSize8sp: Button = findViewById(R.id.button_size8SP)
        val buttonSize24sp: Button = findViewById(R.id.button_size24SP)
        val buttonWhiteBackground: Button = findViewById(R.id.button_whiteBackground)
        val buttonYellowBackground: Button = findViewById(R.id.button_yellowBackground)

        buttonBlackText.setOnClickListener {
            editText.setTextColor(Color.BLACK)
        }
        buttonRedText.setOnClickListener {
            editText.setTextColor(Color.RED)
        }
        buttonSize8sp.setOnClickListener {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f)
        }
        buttonSize24sp.setOnClickListener {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        }
        buttonWhiteBackground.setOnClickListener {
            editText.setBackgroundColor(Color.WHITE)
        }
        buttonYellowBackground.setOnClickListener {
            editText.setBackgroundColor(Color.parseColor("#AB915B"))
        }
    }
}