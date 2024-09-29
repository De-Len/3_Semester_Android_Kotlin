package com.example.nestedlayouts

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var textViewIds: Array<Int>
    private var clickCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textViewIds = arrayOf(
            R.id.layout1TextView1, R.id.layout1TextView2, R.id.layout1TextView3,
            R.id.layout2TextView1, R.id.layout2TextView2, R.id.layout2TextView3,
            R.id.constraintTextView1, R.id.constraintTextView2, R.id.constraintTextView3
        )

        hideAllTextViews()
        findViewById<Button>(R.id.button_roll).setOnClickListener {
            hideAllTextViews()
            showNextTextView()
        }
    }

    private fun hideAllTextViews() {
        textViewIds.forEach { id ->
            findViewById<TextView>(id).visibility = View.INVISIBLE
        }
    }

    private fun showNextTextView() {
        val index = clickCounter % 3
        for (i in textViewIds.indices step 3) {
            val textView = findViewById<TextView>(textViewIds[i + index])
            textView.visibility = View.VISIBLE
            textView.text = (clickCounter + 1).toString()
        }
        ++clickCounter
    }
}