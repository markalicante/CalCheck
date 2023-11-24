package com.example.calcheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class RegistrationForm : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_form)

        var buttonRegister : Button = findViewById(R.id.btnRegister)

        buttonRegister.setOnClickListener(){
            try{



            } catch(e : Exception){
                Log.e("error_espina", e.message.toString())
            }
        }

    }
}