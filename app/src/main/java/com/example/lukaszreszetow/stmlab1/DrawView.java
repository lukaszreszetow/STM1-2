package com.example.lukaszreszetow.stmlab1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
    String TAG = "DRAW";
    int width;
    int height;
    int przesuniecie = 10;
    double przyspieszenie = 1.5;
    Boolean graWystartowala = false;
    int radius = 30;
    int szerokoscPaletki;
    int wysokoscPaletki;
    Point pozycjaPileczki;
    Point pozycjaPaletki1;
    Point pozycjaPaletki2;
    Paint paint;
    Paint paintPaletka;
    Boolean odblokowanoOnDraw = false;
    MainActivity.WektorPileczki wektorPileczki = new MainActivity.WektorPileczki(2, 2);
    ClientActivity clientActivity;
    ServerActivity serverActivity;
    MainActivity.DaneOdSerwera daneOdSerwera;

    public DrawView(Context context, int width, int height, @Nullable ClientActivity clientActivity, @Nullable ServerActivity serverActivity) {
        super(context);
        paint = new Paint();
        paintPaletka = new Paint();
        this.width = width;
        this.height = height;

        szerokoscPaletki = (int) (width * 0.2);
        wysokoscPaletki = (int) (height * 0.01);
        pozycjaPileczki = new Point(width / 2, height / 2);
        pozycjaPaletki1 = new Point((int) (width * 0.5 - szerokoscPaletki * 0.5), 300);
        pozycjaPaletki2 = new Point((int) (width * 0.5 - szerokoscPaletki * 0.5), height - 300 - wysokoscPaletki);
        daneOdSerwera = new MainActivity.DaneOdSerwera(pozycjaPaletki1, pozycjaPileczki);

        if (clientActivity != null) {
            this.clientActivity = clientActivity;
        }

        if (serverActivity != null) {
            this.serverActivity = serverActivity;
            serverActivity.odpowiedzClientowi(daneOdSerwera);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (serverActivity != null) {
            logika();
            daneOdSerwera.setPozycjaPaletkiSerwera(pozycjaPaletki1);
            daneOdSerwera.setPozycjaPileczki(pozycjaPileczki);
            serverActivity.odpowiedzClientowi(daneOdSerwera);
        } else {
        }
        paint.setARGB(255, 0, 0, 0);
        paintPaletka.setARGB(255, 255, 0, 0);
        canvas.drawRect(0, 0, width, height, paint);
        canvas.drawRect(pozycjaPaletki1.x, pozycjaPaletki1.y, pozycjaPaletki1.x + szerokoscPaletki, pozycjaPaletki1.y + wysokoscPaletki, paintPaletka);
        canvas.drawRect(pozycjaPaletki2.x, pozycjaPaletki2.y, pozycjaPaletki2.x + szerokoscPaletki, pozycjaPaletki2.y + wysokoscPaletki, paintPaletka);
        canvas.drawCircle(pozycjaPileczki.x, pozycjaPileczki.y, radius, paintPaletka);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (clientActivity != null) {
            if (!graWystartowala) {
                runDrawing.run();
                graWystartowala = true;
            }
            if (event.getX() <= width / 2) {
                if (pozycjaPaletki2.x - przesuniecie > 0) {
                    pozycjaPaletki2.x -= przesuniecie;
                }
            } else {
                if (pozycjaPaletki2.x + przesuniecie + szerokoscPaletki < width) {
                    pozycjaPaletki2.x += przesuniecie;
                }
            }
        }

        if (serverActivity != null) {
            if (event.getX() <= width / 2) {
                if (pozycjaPaletki1.x - przesuniecie > 0) {
                    pozycjaPaletki1.x -= przesuniecie;
                }
            } else {
                if (pozycjaPaletki1.x + przesuniecie + szerokoscPaletki < width) {
                    pozycjaPaletki1.x += przesuniecie;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runDrawing = new Runnable() {
        public void run() {
            clientActivity.executeClient(pozycjaPaletki2);
            handler.postDelayed(this, 10);
        }
    };

    public void logika() {
        double nowyX = pozycjaPileczki.x + wektorPileczki.getX();
        double nowyY = pozycjaPileczki.y + wektorPileczki.getY();

        if (nowyX > radius && nowyX < width - radius) {
            pozycjaPileczki.x = (int) nowyX;
        } else {
            wektorPileczki.setX(wektorPileczki.getX() * -1);
        }

        if (((nowyY - radius > 292.5) && (nowyY - radius < 307.5)) &&
                (pozycjaPileczki.x < pozycjaPaletki1.x + szerokoscPaletki) &&
                (pozycjaPileczki.x > pozycjaPaletki1.x)) {
            wektorPileczki.setY(wektorPileczki.getY() * -1);
            if (Math.abs(wektorPileczki.getY() * przyspieszenie) < 8) {
                wektorPileczki.setY(wektorPileczki.getY() * przyspieszenie);
            }
        }


        if (((nowyY + radius > height - 307.5) && (nowyY + radius < height - 292.5)) &&
                (pozycjaPileczki.x < pozycjaPaletki2.x + szerokoscPaletki) &&
                (pozycjaPileczki.x > pozycjaPaletki2.x)) {
            wektorPileczki.setY(wektorPileczki.getY() * -1);
            if (Math.abs(wektorPileczki.getY() * przyspieszenie) < 8) {
                wektorPileczki.setY(wektorPileczki.getY() * przyspieszenie);
            }
        }

        if (nowyY < 0 || nowyY > height) {
            if (serverActivity != null) {
                serverActivity.koniecGry = true;
            }
        }
        //pozycjaPileczki.x += wektorPileczki.getX() * 10;
        pozycjaPileczki.y += wektorPileczki.getY();
    }
}