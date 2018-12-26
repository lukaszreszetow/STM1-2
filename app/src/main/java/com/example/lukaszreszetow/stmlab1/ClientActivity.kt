package com.example.lukaszreszetow.stmlab1

import android.graphics.Canvas
import android.graphics.Point
import android.hardware.Sensor
import android.os.AsyncTask
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewTreeObserver
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ClientActivity : AppCompatActivity() {

    lateinit var client: Client
    lateinit var drawView: DrawView
    lateinit var ip: String
    var direction: MainActivity.Direction = MainActivity.Direction.UNKNOWN
    var positionOfEnemy = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        setupSensor()
        val layout = findViewById<ConstraintLayout>(R.id.clientLayout)
        layout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val height = layout.height //height is ready
                val metrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(metrics)
                val width = metrics.widthPixels
                drawView = DrawView(this@ClientActivity, width, height, this@ClientActivity, null)
                layout.addView(drawView)
            }
        })
        ip = intent.getStringExtra("IP")
    }

    @Synchronized
    fun executeClient(positionX: Int) {
        client = Client(ip, 8080, this)
        if (client.status != AsyncTask.Status.RUNNING) {
            client.execute(positionX)
        }
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

    fun zmianaPozycjiSerwera(i: Int) {
        positionOfEnemy = i
    }

    override fun onBackPressed() {
        finish()
    }
}
