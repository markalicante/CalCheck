package com.example.calcheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    //
    lateinit var databaseReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Declaration of Firebase DB Instance
        databaseReference = FirebaseDatabase.getInstance().getReference("DatabaseName")

        var editTextUNameLogin : EditText = findViewById(R.id.edtTextUNameLogin)
        var editTextPWLogin : EditText = findViewById(R.id.edtTextPWLogin)

        var buttonLogin : Button = findViewById(R.id.btnLogin)

        buttonLogin.setOnClickListener(){
            try{



            } catch(e : Exception){
                Log.e("Error_CalCheck", e.message.toString())
            }
        }

    }
}