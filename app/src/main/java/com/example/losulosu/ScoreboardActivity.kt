package com.example.losulosu

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.losulosu.DB.DB.DatabaseHandler
import com.example.losulosu.DB.List.ListAdapter
import com.example.losulosu.DB.Model.EmpModelClass


class ScoreboardActivity : AppCompatActivity(), SensorEventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)
        setUpSensor()

        val table2 = findViewById<ListView>(R.id.listWyniki)
        val backButton = findViewById<Button>(R.id.buttonBack)
        viewRecord(table2)

        backButton.setOnClickListener {
            finish()
        }
    }

    private lateinit var sensorManager: SensorManager
    var brightness: Sensor? = null

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, brightness, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun setUpSensor(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if(event.sensor?.type== Sensor.TYPE_LIGHT){
            if(event.values[0] < 100.0){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    private fun viewRecord(lv: ListView){
        val databaseHandler = DatabaseHandler(this)
        val emp: List<EmpModelClass> = databaseHandler.viewPlayer()
        val empArrayLogin = Array(emp.size){"null"}
        val empArrayScore = Array(emp.size){"0"}
        for((index, e) in emp.withIndex()){
            empArrayLogin[index] = e.login
            empArrayScore[index] = e.score.toString()
        }
        val myListAdapter = ListAdapter(this,empArrayLogin,empArrayScore)
        lv.adapter = myListAdapter
    }
}