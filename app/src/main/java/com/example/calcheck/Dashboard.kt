package com.example.calcheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView

class Dashboard : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var progressText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        progressBar = findViewById(R.id.progress_bar)
        progressText = findViewById(R.id.progress_text)

        val progressValue = 50 // You can change this to any value between 0 and 100
        progressBar.progress = progressValue
        progressText.text = progressValue.toString()


    }
}