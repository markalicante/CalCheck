package com.example.calcheck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView.RecyclerListener
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class FoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
    }




    fun prevPage(view: View){
        val page2 = Intent(this, Dashboard::class.java)
        startActivity(page2)
    }

}