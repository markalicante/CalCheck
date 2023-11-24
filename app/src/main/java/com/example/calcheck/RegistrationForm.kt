package com.example.calcheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class RegistrationForm : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_form)

        var editTextUNameRegister : EditText = findViewById(R.id.edtTextUNameRegister)
        var editTextPWLoginRegister : EditText = findViewById(R.id.edtTextPWRegister)

        var buttonRegister : Button = findViewById(R.id.btnRegister)

        buttonRegister.setOnClickListener(){
            try{



            } catch(e : Exception){
                Log.e("Error_CalCheck", e.message.toString())
            }
        }

    }
}