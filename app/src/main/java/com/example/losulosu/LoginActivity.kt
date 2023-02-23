package com.example.losulosu

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.losulosu.DB.DB.DatabaseHandler
import com.example.losulosu.DB.Model.EmpModelClass
import java.lang.Exception

class LoginActivity : AppCompatActivity(), SensorEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editLogin = findViewById<EditText>(R.id.editTextLogin)
        val editPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonWyniki = findViewById<Button>(R.id.buttonWyniki)
        val builder = AlertDialog.Builder(this)

        setUpSensor()

        buttonLogin.setOnClickListener {
            if(editLogin.text.isEmpty() || editPassword.text.isEmpty()){
                builder.setTitle("Błąd")
                builder.setMessage("Podaj login oraz hasło")
                builder.setPositiveButton("OK", null)
                builder.show()
                editPassword.text.clear()
                editLogin.text.clear()
            }
            else{
                val login = editLogin.text.toString()
                val password = editPassword.text.toString()
                val databaseHandler = DatabaseHandler(this)
                try {
                    val status = databaseHandler.findPlayer(login)
                    if(status[0].password != password) {
                        Toast.makeText(
                            applicationContext,
                            "Niepoprawne Hasło",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else{
                        val intent = Intent(this, MainActivity::class.java)
                        setRecord(status[0].login,status[0].password,status[0].score)
                        startActivity(intent)
                        editLogin.text.clear()
                        editPassword.text.clear()
                    }
                }catch (e: Exception){
                    builder.setTitle("Błąd")
                    builder.setMessage("Brak użytkownika w bazie")
                    builder.setPositiveButton("OK", null)
                    builder.show()
                    editPassword.text.clear()
                    editLogin.text.clear()
                }
            }
        }
        buttonRegister.setOnClickListener {
            if(editLogin.text.isEmpty() || editPassword.text.isEmpty()){
                builder.setTitle("Błąd")
                builder.setMessage("Podaj login oraz hasło")
                builder.setPositiveButton("OK", null)
                builder.show()
                editPassword.text.clear()
                editLogin.text.clear()
            }
            else{
                val login = editLogin.text.toString()
                val password = editPassword.text.toString()
                val score = 0
                val databaseHandler = DatabaseHandler(this)
                try {
                    val status = databaseHandler.findPlayer(login)
                    if(status[0].password != password) {
                        Toast.makeText(
                            applicationContext,
                            "Niepoprawne Hasło",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    builder.setTitle("Błąd")
                    builder.setMessage("Użytkownik o podanym loginie już istnieje")
                    builder.setPositiveButton("OK", null)
                    builder.show()
                    editPassword.text.clear()
                    editLogin.text.clear()
                }catch (e: Exception){
                    databaseHandler.addPlayer(EmpModelClass(login,password,score))
                    val intent = Intent(this, MainActivity::class.java)
                    setRecord(login,password,score)
                    startActivity(intent)
                    editLogin.text.clear()
                    editPassword.text.clear()
                }
            }
        }
        buttonWyniki.setOnClickListener {
            val intent = Intent(this, LoadingActivity::class.java)
            startActivity(intent)
            editLogin.text.clear()
            editPassword.text.clear()
        }
    }

    private fun setRecord(a: String, b: String, c: Int){
        val sharedScore = this.getSharedPreferences("com.example.myapplication.shared",0)
        val edit = sharedScore.edit()
        edit.putString("login", a)
        edit.putString("password", b)
        edit.putInt("score", c)
        edit.apply()
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
}