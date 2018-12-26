package com.example.lukaszreszetow.stmlab1

import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.os.AsyncTask
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast

import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import com.github.pwittchen.reactivesensors.library.SensorNotFoundException

import java.io.OutputStream
import java.io.PrintStream
import java.net.ServerSocket
import java.net.Socket
import java.util.function.Consumer

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ServerActivity : AppCompatActivity() {

    lateinit var server: Server
    lateinit var drawView: DrawView
    var koniecGry: Boolean? = false
    var direction: MainActivity.Direction = MainActivity.Direction.UNKNOWN
    var positionOfEnemy = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)

        setupSensor()
        val layout = findViewById<ConstraintLayout>(R.id.serverLayout)
        layout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val height = layout.height //height is ready
                val metrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(metrics)
                val width = metrics.widthPixels
                drawView = DrawView(this@ServerActivity, width, height, null, this@ServerActivity)
                layout.addView(drawView)
            }
        })

        server = Server(this)
        Toast.makeText(this, server.ipAddress, Toast.LENGTH_LONG).show()
    }

    private fun setupSensor() {
        ReactiveSensors(this).observeSensor(Sensor.TYPE_ACCELEROMETER)
            .subscribeOn(Schedulers.computation())
            .filter(ReactiveSensorFilter.filterSensorChanged())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val event = it.sensorEvent
                direction = if (event.values[0] > 0) {
                    MainActivity.Direction.LEFT
                } else {
                    MainActivity.Direction.RIGHT
                }
            }
    }

    fun zmianaPozycjiClienta(i: Int) {
        positionOfEnemy = i
        if (!drawView.graWystartowala) {
            drawView.runDrawing.run()
            drawView.graWystartowala = true
        }
    }

    fun getPositionOfMyShip(): Int {
        return drawView.spaceshipPos.x
    }

    override fun onDestroy() {
        super.onDestroy()
        server.onDestroy()
    }

    override fun onBackPressed() {
        finish()
    }
}
