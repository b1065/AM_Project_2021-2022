package com.example.losulosu

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.losulosu.DB.DB.DatabaseHandler
import com.example.losulosu.DB.Model.EmpModelClass

class MainActivity : AppCompatActivity(), SensorEventListener {

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("shots", shots)
        outState.putInt("answer", answer)
        outState.putString("hint", textHint.text as String?)
    }

    var shots = 0
    var answer = 0
    lateinit var textHint: TextView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpSensor()
        val builder = AlertDialog.Builder(this)
        val buttonShot = findViewById<Button>(R.id.strzalButton)
        val buttonNew = findViewById<Button>(R.id.newGameButton)
        val buttonLogout = findViewById<Button>(R.id.logoutButton)
        val buttonWyniki = findViewById<Button>(R.id.wynikiButton)
        val textCount = findViewById<TextView>(R.id.countText)
        val textPoints = findViewById<TextView>(R.id.pointsCount)
        val textEdit = findViewById<EditText>(R.id.editText)
        textHint = findViewById<TextView>(R.id.helpText)
        if(savedInstanceState!=null)
        {
            shots = savedInstanceState.getInt("shots")
            answer = savedInstanceState.getInt("answer")
            textHint.visibility = TextView.VISIBLE
            textHint.text = savedInstanceState.getString("hint")
        }
        else{
            answer = (0..20).random()
            shots = 0
        }
        var currpoints: Int
        var points: Int
        var login: String
        var password: String
        val databaseHandler = DatabaseHandler(this)
        val sharedScore = this.getSharedPreferences("com.example.myapplication.shared",0)
        val edit = sharedScore.edit()
        textCount.text=shots.toString()

        currpoints = getRecord()
        textPoints.text=(currpoints).toString()

        buttonLogout.setOnClickListener {
            edit.clear()
            edit.apply()
            finish()
        }

        buttonWyniki.setOnClickListener {
            val intent = Intent(this, LoadingActivity::class.java)
            startActivity(intent)
        }

        buttonNew.setOnClickListener {
            textEdit.text.clear()
            answer= (0..20).random()
            shots=0
            textCount.text = shots.toString()
            if (textHint.visibility == TextView.GONE)
                textHint.visibility = TextView.VISIBLE
            textHint.text = "NOWA GRA"
        }

        buttonShot.setOnClickListener {
            if (textEdit.text.isEmpty() || Integer.parseInt(textEdit.text.toString()) < 0 || Integer.parseInt(
                    textEdit.text.toString()
                ) > 20){
                builder.setTitle("Błąd")
                builder.setMessage("Podaj liczbę z zakresu <0;20>")
                builder.setPositiveButton("OK", null)
                builder.show()
                textEdit.text.clear()
            }
            else{
                if(textEdit.text.toString().toInt()==answer){
                    points = when(shots){
                        0 -> 5
                        in 1..3 -> 3
                        4,5 -> 2
                        else -> 1
                    }
                    currpoints+=points
                    textPoints.text=(currpoints).toString()
                    setRecord(currpoints)
                    builder.setTitle("Gratulacje")
                    builder.setMessage("Udało Ci się zgadnąć tajemniczą liczbę!\n Ilość prób: "+(shots+1)+"\nLiczba zdobytych punktów: "+points)
                    builder.setPositiveButton("Yay", null)
                    builder.show()
                    login = sharedScore.getString("login", "Default") ?: "Not Set"
                    password = sharedScore.getString("password", "Default") ?: "Not Set"
                    currpoints = sharedScore.getInt("score",0)
                    databaseHandler.updatePlayer(EmpModelClass(login,password,currpoints))
                    textEdit.text.clear()
                    answer= (0..20).random()
                    shots=0
                    textCount.text = shots.toString()
                    if (textHint.visibility == TextView.GONE)
                        textHint.visibility = TextView.VISIBLE
                    textHint.text = "NOWA GRA"
                }
                else{
                    if (textHint.visibility == TextView.GONE)
                        textHint.visibility = TextView.VISIBLE
                    if(Integer.parseInt(textEdit.text.toString()) >answer){
                        textHint.text = "Tajemnicza liczba jest mniejsza od podanej."
                    }
                    else{
                        textHint.text = "Tajemnicza liczba jest większa od podanej."
                    }
                    textEdit.text.clear()
                    shots++
                    textCount.text = shots.toString()
                    if (shots==10){
                        builder.setTitle("Przegrałeś")
                        builder.setMessage("Nie udało się odgadnąć tajemniczej liczby w 10 próbach!\n Rozpoczynam nową grę.")
                        builder.setPositiveButton("OK", null)
                        builder.show()
                        textEdit.text.clear()
                        textEdit.text.clear()
                        answer= (0..20).random()
                        shots=0
                        textCount.text = shots.toString()
                        if (textHint.visibility == TextView.GONE)
                            textHint.visibility = TextView.VISIBLE
                        textHint.text = "NOWA GRA"
                    }
                }
            }
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

    private fun setRecord(currpoints: Int){
        val sharedScore = this.getSharedPreferences("com.example.myapplication.shared",0)
        val edit = sharedScore.edit()
        edit.putInt("score", currpoints)
        edit.apply()
    }

    private fun getRecord():Int{
        val sharedScore = this.getSharedPreferences("com.example.myapplication.shared",0)
        return sharedScore.getInt("score", 0)
    }
}