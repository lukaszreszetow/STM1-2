package com.example.lukaszreszetow.stmlab1;

import android.graphics.Point;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends AppCompatActivity {

    Server server;
    DrawView drawView;
    MainActivity.DaneOdSerwera dane;
    Boolean koniecGry = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        drawView = new DrawView(this, width, height, null, this);
        ConstraintLayout layout = findViewById(R.id.serverLayout);
        layout.addView(drawView);

        server = new Server(this);
        Toast.makeText(this, server.getIpAddress(), Toast.LENGTH_LONG).show();
    }

    public void rysujSerwer(Point point){
        drawView.pozycjaPaletki2 = point;
        drawView.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }

    public void odpowiedzClientowi(MainActivity.DaneOdSerwera daneOdSerwera) {
        this.dane = daneOdSerwera;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
