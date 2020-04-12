package com.example.smartplant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = Firebase.database.reference

        val snooze: Button = findViewById(R.id.snooze)
        val test : TextView = findViewById(R.id.test)

        database.child("PI_01_CONTROL").child("buzzer").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                test.text = p0.getValue().toString()
                if(test.text == "1")
                    snooze.setVisibility(View.VISIBLE)
                else
                    snooze.setVisibility(View.GONE)
            }
        })

        snooze.setOnClickListener{
            database.child("PI_01_CONTROL").child("buzzer").setValue("0")
        }
    }

    /*override fun onStart() {
        super.onStart()
        val dateListener = object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val buzzer = dataSnapshot.getValue()
            }
        }
    }*/


}
