package com.example.shasank.canvasdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.lang.reflect.Field;

class Character {
    private int angle;
    private boolean rotate;
    private float y;
    private float x;
    private Bitmap[] image;
    private int imageIndex;
    private int displayWidth,displayHeight;

    Character(Context c, int displayHeight, int displayWidth, int characterCode) {
        this.displayHeight=displayHeight;
        this.displayWidth=displayWidth;
        int j = 0;
        angle = 200;
        imageIndex = 0;
        image = new Bitmap[7];
        for (char i = 'a'; i <= 'g'; i++, j++) {
            String id = i + String.valueOf(characterCode);
            image[j] = BitmapFactory.decodeResource(c.getResources(), getResId(id, R.drawable.class));
        }
        x = displayWidth / 2;
        y = displayHeight/2;
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
    }

    void update(long waitTime) {
        if (angle != 200 && waitTime >= 200) {
            imageIndex++;
            if (imageIndex == 7)
                imageIndex = 1;
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

    void setX(float cosTheta){
        x+=cosTheta;
        if(x<0)
            x=0;
        if(x>displayWidth-image[0].getWidth())
            x=displayWidth-image[0].getWidth();
    }
    void setY(float sinTheta){
        y+=sinTheta;
        if(y<0)
            y=0;
        if(y>displayHeight-image[0].getHeight())
            y=displayHeight-image[0].getHeight()-1;
    }
    float getX(){return x;}
    float getY(){return y;}



}
