package com.example.lukaszreszetow.stmlab1;

import android.graphics.Canvas;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class ClientActivity extends AppCompatActivity {

    Client client;
    DrawView drawView;
    Canvas canvas;
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        drawView = new DrawView(this, width, height, this, null);
        ConstraintLayout layout = findViewById(R.id.clientLayout);
        layout.addView(drawView);
        ip = getIntent().getStringExtra("IP");
    }

    synchronized public void executeClient(Point point) {
        client = new Client(ip, 8080, this);
        if(client.getStatus() != AsyncTask.Status.RUNNING) {
            client.execute(point);
        }
    }

    public void triggerRysowania(MainActivity.DaneOdSerwera result) {
        drawView.pozycjaPaletki1 = result.getPozycjaPaletkiSerwera();
        drawView.pozycjaPileczki = result.getPozycjaPileczki();
        drawView.odblokowanoOnDraw = true;
        drawView.invalidate();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
