package com.example.smartplant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = Firebase.database.reference

        val snooze: Button = findViewById(R.id.snooze)
        val test: TextView = findViewById(R.id.test)

        database.child("PI_01_CONTROL").child("buzzer").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                test.text = p0.getValue().toString()
                if (test.text == "1")
                    snooze.setVisibility(View.VISIBLE)
                else
                    snooze.setVisibility(View.GONE)
            }
        })

        snooze.setOnClickListener {
            database.child("PI_01_CONTROL").child("buzzer").setValue("0")
        }


        //LED part
        btnAuto.setOnClickListener {
            //check humid
            //while loop or for loop
            /*val newhumid = 0.0
            val humid = 0

            while (humid != newhumid) {
                val i = 1//a(humid, newhumid)
                if (i >= 4) {
                    database.child("PI_01_CONTROL").child("led").setValue("1")
                } else if (i <= 3) {
                    database.child("PI_01_CONTROL").child("led").setValue("0")
                }
                i + 1
            }*/
            val auto: Button = findViewById(R.id.btnAuto)
            val txtauto: TextView = findViewById(R.id.txtAuto)
            database.child("PI_01_CONTROL").child("led").addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    txtauto.text = p0.getValue().toString()
                    if (txtauto.text == "1")
                        auto.setVisibility(View.VISIBLE)
                    else
                        auto.setVisibility(View.GONE)
                }
            })
        }

        btnManual.setOnClickListener {

            database.child("PI_01_CONTROL").child("led").setValue("1")

            val timer = object: CountDownTimer(15000, 2000) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    database.child("PI_01_CONTROL").child("led").setValue("0")
                }
            }
            timer.start()

        }

       /* val future = doAsync {
            // do your background thread task
            result = someTask()

            uiThread {
                // use result here if you want to update ui
                updateUI(result)
            }
        }
        future.cancel(true)*/
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
