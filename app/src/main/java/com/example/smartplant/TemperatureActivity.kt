package com.example.smartplant

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.temperature.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TemperatureActivity :  AppCompatActivity() {


    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.temperature)
        database = Firebase.database.reference

        btnupdate.setOnClickListener{
            CurrenTempeture()
        }
        btnback.setOnClickListener{
            val intent= Intent (this,MainActivity::class.java)
            startActivity(intent)
        }

    }


    private fun CurrenTempeture() {

        val current = LocalDateTime.now()

        //format
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val hourFormat = DateTimeFormatter.ofPattern("HH")
        val minFormat = DateTimeFormatter.ofPattern("mm")
        val secondFormat = DateTimeFormatter.ofPattern("ss")

        val currentDate = current.format(formatter)
        val currentHours = current.format(hourFormat)
        val currentMin = current.format(minFormat)
        val currentSecond = current.format(secondFormat)
        var second = currentSecond.toInt()

        if (second % 10 != 0) {
            second -= (second % 10)
        }

        FirebaseDatabase.getInstance().reference
            .child("PI_01_$currentDate")
            .child("$currentHours") //hours
            .child("$currentMin" + "$second")  //min+second
            .child("tempe")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    tempeture.text = "Tempeture : "+ p0.getValue().toString() + "°C"
                }
            })

        temdate.text = "Date : $currentDate"
        temhour.text = "Time : $currentHours".toString()+":$currentMin".toString()+":$currentSecond".toString()
    }

}