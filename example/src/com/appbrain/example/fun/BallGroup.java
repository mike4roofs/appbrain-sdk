package com.appbrain.example.fun;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BallGroup {
    final int width;
    final int height;
    List<Ball> balls;
    private Bitmap bitmap;
    private final int step;

    public BallGroup(int width, int height, int step) {
        this.step = step;
        this.width = width;
        this.height = height;
        balls = new ArrayList<Ball>(0);

        createText(balls, "AppLift");

    }

    private void createText(List<Ball> ballsArrayList, String text) {
        int w = (width / step) * step;
        int h = (height / step) * step;

        bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((int) (h * 0.5));
        paint.setFakeBoldText(true);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        bitmap.eraseColor(Color.BLACK);
        canvas.drawText(text, w / 2, (int) (h * 0.6), paint);

        int pix[] = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        float s = 1.2f;
        float[] hsv = new float[3];
        
        int midx = w / 2, midy = h / 2;

        for (int y = 0; y < h; y += step) {
            for (int x = 0; x < w; x += step) {
                int offset = x + y * w;
                long sum = 0;
                for (int dy = 0; dy < step; dy++) {
                    for (int dx = 0; dx < step; dx++) {
                        sum += (pix[offset + dx + dy * w] & 0xff);
                    }
                }

                sum = sum / (step * step);
                int radius = (int) (step * sum / 255);
                if (radius > 1) {
                    hsv[0] = 360 * x / width;
                    hsv[1] = 1;
                    hsv[2] = 1;

                    ballsArrayList.add(new Ball(s * (x - midx) + midx, s * (y - midy) + midy, 0, radius, Color.HSVToColor(hsv)));
                }
            }
        }
        bitmap = null;
    }

    public void update(float mouseX, float mouseY) {
        Vector d = new Vector(0, 0);
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);
            d.x = mouseX - ball.pos.x;
            d.y = mouseY - ball.pos.y;
            if (d.magSquared() < 100 * 100) {
                Vector.sub(ball.pos, d, ball.goal);
            } else {
                ball.goal.set(ball.startPos);
            }

            ball.update();
        }
    }

    public void draw(Canvas context) {
        // context.drawBitmap(bitmap, 0, 0, null);
        for (int i = balls.size() - 1; i >= 0; i--) {
            balls.get(i).draw(context);
        }
    }
}