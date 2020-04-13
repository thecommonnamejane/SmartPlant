package com.example.smartplant

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Time
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = Firebase.database.reference






        btnHumidity.setOnClickListener {
            val intent = Intent(this, Humidity::class.java)
            startActivity(intent)
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

            val timer = object : CountDownTimer(15000, 2000) {
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

    override fun onStart() {
        super.onStart()
        val snooze: Button = findViewById(R.id.snooze)
        val test: TextView = findViewById(R.id.test)
        val tempSwitch: Switch = findViewById(R.id.tempSwitch)
        val timer = Timer()


        //format
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val hourFormat = DateTimeFormatter.ofPattern("HH")
        val minFormat = DateTimeFormatter.ofPattern("mm")
        val secondFormat = DateTimeFormatter.ofPattern("ss")

        tempSwitch.setOnClickListener {
            if (tempSwitch.isChecked) {
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        val current = LocalDateTime.now()

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
                            .addValueEventListener(
                                object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        hiddenTemp.text = p0.getValue().toString() + "%"
                                    }
                                })
                        if (hiddenTemp.text == "36.0%") {
                            database.child("PI_01_CONTROL").child("buzzer").setValue("0")
                        } else if (hiddenTemp.text == "null%")
                            database.child("PI_01_CONTROL").child("buzzer").setValue("0")
                        else if (hiddenTemp.text != "36.0%") {
                            database.child("PI_01_CONTROL").child("buzzer").setValue("1")
                        } else if (hiddenTemp.text == "1") {
                            database.child("PI_01_CONTROL").child("buzzer").setValue("1")
                        }
                    }
                }, 1, 60000)
            }
            else{
                timer.cancel()
                timer.purge()
            }
        }

        snooze.setOnClickListener {
            hiddenTemp.text = "1"
            database.child("PI_01_CONTROL").child("buzzer").setValue("0")
        }

        database.child("PI_01_CONTROL").child("buzzer").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                test.text = p0.getValue().toString()
                if (test.text == "1")
                    snooze.setVisibility(View.VISIBLE)
                else
                    snooze.setVisibility(View.GONE)
            }
        })
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



