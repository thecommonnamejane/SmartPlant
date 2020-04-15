package com.example.smartplant

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = Firebase.database.reference


        btnHumidity.setOnClickListener {
            val intent = Intent(this, Humidity::class.java)
            startActivity(intent)
        }

        btntempe.setOnClickListener {
            val intent = Intent(this, TemperatureActivity::class.java)
            startActivity(intent)
        }

        //LED part
        fun readHum() {

            val crt = LocalDateTime.now()
            //format
            val formatter = DateTimeFormatter.BASIC_ISO_DATE
            val hourFormat = DateTimeFormatter.ofPattern("HH")
            val minFormat = DateTimeFormatter.ofPattern("mm")
            val secondFormat = DateTimeFormatter.ofPattern("ss")

            val currentDate = crt.format(formatter)
            val currentHours = crt.format(hourFormat)
            val currentMin = crt.format(minFormat)
            val currentSecond = crt.format(secondFormat)
            var second = currentSecond.toInt()

            if (second % 10 != 0) {
                second -= (second % 10)
            }

            FirebaseDatabase.getInstance().reference
                .child("PI_01_$currentDate")
                .child("$currentHours") //hours
                .child("$currentMin" + "$second")  //min+second
                .child("humid")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        txtAuto.text = p0.getValue().toString() + "%"
                        if (txtAuto.text.toString() >= "40") {
                            database.child("PI_01_CONTROL").child("led").setValue("0")
                        } else {
                            database.child("PI_01_CONTROL").child("led").setValue("1")
                        }
                    }
                })
            database.child("PI_01_CONTROL").child("led").setValue("0")

        }
        btnAuto.setOnClickListener {
            readHum()
        }
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
                    auto.setVisibility(View.INVISIBLE)
            }
        })
        btnManual.setOnClickListener{

            database.child("PI_01_CONTROL").child("led").setValue("1")

            val timer = object : CountDownTimer(15000, 2000) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    database.child("PI_01_CONTROL").child("led").setValue("0")
                }
            }
            timer.start()

        }
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
                        val now = LocalDateTime.now()

                        val nowDate = now.format(formatter)
                        val nowHours = now.format(hourFormat)
                        val nowMin = now.format(minFormat)
                        val nowSecond = now.format(secondFormat)
                        var second = nowSecond.toInt()

                        if (second % 10 != 0) {
                            second -= (second % 10)
                        }

                        FirebaseDatabase.getInstance().reference
                            .child("PI_01_$nowDate")
                            .child("$nowHours") //hours
                            .child("$nowMin" + "$second")  //min+second
                            .child("tempe")
                            .addValueEventListener(
                                object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        hiddenTemp.text = p0.getValue().toString() + "%"
                                        if (p0.getValue()==null)
                                            database.child("PI_01_CONTROL").child("buzzer").setValue("0")
                                        else if(p0.getValue()!=36.0)
                                            database.child("PI_01_CONTROL").child("buzzer").setValue("1")
                                    }
                                })
                         if (hiddenTemp.text == "1") {
                            database.child("PI_01_CONTROL").child("buzzer").setValue("1")
                        }
                    }
                }, 1, 30000)
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
                    snooze.setVisibility(View.INVISIBLE)
            }
        })
    }
}
