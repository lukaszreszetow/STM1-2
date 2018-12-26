package com.example.lukaszreszetow.stmlab1

import android.content.Intent
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clientBT.setOnClickListener {
            startActivity(Intent(this, ClientActivity::class.java).apply {
                putExtra("IP", ipET.text.toString())
            })
        }
        serverBT.setOnClickListener {
            startActivity(Intent(this, ServerActivity::class.java))
        }
    }

    enum class Direction{
        LEFT, RIGHT, UNKNOWN
    }
}
