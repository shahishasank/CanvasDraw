package com.example.shasank.canvasdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.lang.reflect.Field;

class Character {
    private int angle;
    private int theta;
    private boolean rotate;
    private float y;
    private float x;
    private Bitmap[] image;
    private int imageIndex;

    Character(Context c, int t, int l, int characterCode) {
        int j = 0;
        angle = 200;
        theta = 400;
        imageIndex = 0;
        image = new Bitmap[7];
        for (char i = 'a'; i <= 'g'; i++, j++) {
            String id = i + String.valueOf(characterCode);
            image[j] = BitmapFactory.decodeResource(c.getResources(), getResId(id, R.drawable.class));
        }
        y = t / 2;
        x = l / 2;
    }

    private static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    void draw(Canvas canvas) {
        //Rect rect=new Rect((int)x,(int)y,(int)x+100,(int)y+100);
        if (rotate)
            canvas.drawBitmap(RotateBitmap(image[imageIndex]), x, y, null);
        else
            canvas.drawBitmap(image[imageIndex], x, y, null);

        Paint p = new Paint();
        p.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(theta) + "=" + String.valueOf(x) + "," + String.valueOf(y), 100, 100, p);
    }

    void update(long waitTime) {
        if (angle != 200 && waitTime >= 200) {
            imageIndex++;
            if (imageIndex == 7)
                imageIndex = 1;
            setXY();
        }
        if (angle == 200)
            imageIndex = 0;
    }

    void setAngle(int angle) {
        this.angle = angle;
    }

    void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    private static Bitmap RotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void setXY() {
        theta=-angle;
        theta = theta < 0 ? 360 + theta : theta;
        float cosTheta = 20 * (float) Math.cos(Math.toRadians(theta));
        float sinTheta = 20 * (float) Math.sin(Math.toRadians(theta));
        x+=cosTheta;
        y+=sinTheta;
    }
}
