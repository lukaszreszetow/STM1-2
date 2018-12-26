package com.example.lukaszreszetow.stmlab1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {

    int width;
    int height;
    Boolean graWystartowala = false;
    Boolean koniecGry = false;
    Paint paint;
    Paint paint2;
    ClientActivity clientActivity;
    ServerActivity serverActivity;
    Point spaceshipPos;
    Point spaceshipPosEnemy;
    List<Point> shotsPos = new ArrayList<>();
    List<Point> shotsPosEnemy = new ArrayList<>();
    int sizeOfImage = 200;
    int licznik = 0;

    public DrawView(Context context, int width, int height, @Nullable ClientActivity clientActivity, @Nullable ServerActivity serverActivity) {
        super(context);
        koniecGry = false;
        paint = new Paint();
        paint2 = new Paint();
        this.width = width;
        this.height = height;
        spaceshipPos = new Point(width / 2 - 100, height - sizeOfImage);
        spaceshipPosEnemy = new Point(width / 2 - 100, 0);

        if (clientActivity != null) {
            this.clientActivity = clientActivity;
        }

        if (serverActivity != null) {
            this.serverActivity = serverActivity;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setARGB(255, 0, 0, 0);
        paint2.setARGB(255, 255, 0, 0);
        canvas.drawRect(0, 0, width, height, paint);

        logika();

        drawShots(canvas);

        Drawable spaceship = getResources().getDrawable(R.drawable.spaceshipwhite, null);
        Drawable spaceshipFliped = getResources().getDrawable(R.drawable.spaceshipfliped, null);
        spaceship.setBounds(spaceshipPos.x, spaceshipPos.y, spaceshipPos.x + 200, spaceshipPos.y + 200);
        spaceshipFliped.setBounds(spaceshipPosEnemy.x, spaceshipPosEnemy.y, spaceshipPosEnemy.x + 200, spaceshipPosEnemy.y + 200);
        spaceship.draw(canvas);
        spaceshipFliped.draw(canvas);
        Log.d("Draw", "Narysowalem statek");
    }

    private void drawShots(Canvas canvas) {
        for (Point point : shotsPos) {
            canvas.drawRect(point.x, point.y, point.x + 10, point.y + 20, paint2);
        }

        for (Point point : shotsPosEnemy) {
            canvas.drawRect(point.x, point.y, point.x + 10, point.y + 20, paint2);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (clientActivity != null) {
            if (!graWystartowala) {
                runDrawing.run();
                graWystartowala = true;
            }
        }
        return super.onTouchEvent(event);
    }

    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runDrawing = new Runnable() {
        public void run() {
            moveShots();
            invalidate();
            calculateIfDead();
            if (clientActivity != null) {
                clientActivity.executeClient(spaceshipPos.x);
            }
            if (licznik++ == 20) {
                licznik = 0;
                addShot();
            }
            if(!koniecGry) {
                handler.postDelayed(this, 10);
            }
        }
    };

    private void moveShots() {
        for (Point point : shotsPos) {
            point.y -= 5;
        }

        for (Point point : shotsPosEnemy) {
            point.y += 5;
        }

        shotsPos = Stream.of(shotsPos).filter(point -> point.y > 0).toList();
        shotsPosEnemy = Stream.of(shotsPosEnemy).filter(point -> point.y < height).toList();

    }

    private void addShot() {
        shotsPos.add(new Point(spaceshipPos.x + 100, spaceshipPos.y - 20));
        shotsPosEnemy.add(new Point(spaceshipPosEnemy.x + 100, spaceshipPosEnemy.y + 220));
    }

    private void calculateIfDead() {
        Activity visibleActivity;
        if(serverActivity != null){
            visibleActivity = serverActivity;
        } else {
            visibleActivity = clientActivity;
        }
        for (Point point : shotsPos) {
            if (point.y < 200) {
                if (point.x > spaceshipPosEnemy.x && point.x < spaceshipPosEnemy.x + 200) {
                    Toast.makeText(visibleActivity, "WYGRALES!", Toast.LENGTH_LONG).show();
                    visibleActivity.finish();
                    koniecGry = true;
                }
            }
        }

        for( Point point : shotsPosEnemy){
            if (point.y > height - 200) {
                if (point.x > spaceshipPos.x && point.x < spaceshipPos.x + 200) {
                    Toast.makeText(visibleActivity, "PRZEGRALES!", Toast.LENGTH_LONG).show();
                    visibleActivity.finish();
                    koniecGry = true;
                }
            }
        }
    }

    public void logika() {

        if (serverActivity != null) {
            if (serverActivity.getDirection() == MainActivity.Direction.LEFT) {
                spaceshipPos.x -= 5;
                if (spaceshipPos.x < 0) {
                    spaceshipPos.x = 0;
                }
            } else {
                spaceshipPos.x += 5;
                if (spaceshipPos.x > width - 200) {
                    spaceshipPos.x = width - 200;
                }
            }
            if (serverActivity.getPositionOfEnemy() != -1) {
                spaceshipPosEnemy.x = serverActivity.getPositionOfEnemy();
            }
        } else {
            if (clientActivity.getDirection() == MainActivity.Direction.LEFT) {
                spaceshipPos.x -= 5;
                if (spaceshipPos.x < 0) {
                    spaceshipPos.x = 0;
                }
            } else {
                spaceshipPos.x += 5;
                if (spaceshipPos.x > width - 200) {
                    spaceshipPos.x = width - 200;
                }
            }
            if (clientActivity.getPositionOfEnemy() != -1) {
                spaceshipPosEnemy.x = clientActivity.getPositionOfEnemy();
            }
        }

    }
}