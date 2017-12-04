package com.example.shasank.canvasdraw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    Bitmap controller;
    Bitmap gameBackground;
    private float backgroundX, backgroundY;
    private final int controllerMoveX, controllerMoveY;
    int startX = 1, startY = 1;
    int displayHeight;
    int displayWidth;
    Rect gameController, controllerRect, gameRect, backReact;
    int controlAngle = 200;
    ControlThread controlThread;
    Character character;

    public MySurfaceView(Context context) {
        super(context);
        displayHeight=Resources.getSystem().getDisplayMetrics().heightPixels;
        displayWidth=Resources.getSystem().getDisplayMetrics().widthPixels;
        controllerMoveX = displayWidth * 3 / 100;
        controllerMoveY = displayHeight * 3 / 100;
        this.setFocusable(true);
        this.getHolder().addCallback(this);
        controller = BitmapFactory.decodeResource(getResources(), R.drawable.inactive);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.back);
        gameBackground = Bitmap.createScaledBitmap(bitmap, displayWidth * 2, displayHeight * 2, false);
        character = new Character(context, displayHeight, displayWidth, 0);
        controlThread = new ControlThread(this, this.getHolder());
        controlThread.setRunningStatus(false);
        controllerRect = new Rect(0, 0, controller.getWidth(), controller.getHeight());
        int l = displayWidth * 5 / 100,
                r = displayWidth * 15 / 100,
                t = displayHeight * 75 / 100,
                b = displayHeight * 9 / 10;
        gameController = new Rect(l, t, r, b);
        gameRect = new Rect(0, 0, displayWidth, displayHeight);
        backReact = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        backgroundX = displayWidth / 2;
        backgroundY = displayHeight / 2;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Thread t = new Thread(controlThread);
        t.start();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                // Parent thread must wait until the end of GameThread.
                this.controlThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawBitmap(gameBackground, -1 * backgroundX, -1 * backgroundY, null);
        character.draw(canvas);
        canvas.drawBitmap(controller, controllerRect, gameController, null);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(backgroundX) + "," + String.valueOf(backgroundY), 100, 100, p);

    }


    @Override
    public boolean performClick() {
        // Calls the super implementation, which generates an AccessibilityEvent
        // and calls the onClick() listener on the view, if any
        super.performClick();

        // Handle the action for the custom click here

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                startX = (int) e.getX();
                startY = (int) e.getY();
                if (gameController.contains(startX, startY)) {
                    controller = BitmapFactory.decodeResource(getResources(), R.drawable.active);
                    Thread t = new Thread(controlThread);
                    t.start();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (gameController.contains(startX, startY)) {
                    int x = (int) e.getX();
                    int y = (int) e.getY();
                    if (Math.abs(startX - x) > controllerMoveX || Math.abs(startY - y) > controllerMoveY) {
                        controlAngle = (int) Math.toDegrees(Math.atan2(startY - y, x - startX));
                        character.setAngle(controlAngle);
                        if (!controlThread.getRunningStatus()) {
                            controlThread.setRunningStatus(true);
                            Thread t = new Thread(controlThread);
                            t.start();
                        }
                    } else {
                        controlAngle = 200;
                        character.setAngle(controlAngle);
                        controlThread.setRunningStatus(false);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (gameController.contains(startX, startY)) {
                    controlAngle = 200;
                    character.setAngle(controlAngle);
                    controller = BitmapFactory.decodeResource(getResources(), R.drawable.inactive);
                    controlThread.setRunningStatus(false);
                    Thread t = new Thread(controlThread);
                    t.start();
                }
                break;
        }
        if (controlAngle != 200) {
            if (Math.abs(controlAngle) > 90)
                character.setRotate(true);
            else
                character.setRotate(false);
        }
        return true;
    }

    public void update(long waitTime) {
        character.update(waitTime);
        if (controlThread.getRunningStatus())
            manageBackgroundRect();
    }

    public void manageBackgroundRect() {
        int theta = -controlAngle;
        theta = theta < 0 ? 360 + theta : theta;
        float cosTheta = 10 * (float) Math.cos(Math.toRadians(theta));
        float sinTheta = 10 * (float) Math.sin(Math.toRadians(theta));
        if (!backgroundEndX())
            backgroundX += cosTheta;
        else
            character.setX(cosTheta);
        if (!backgroundEndY())
            backgroundY += sinTheta;
        else
            character.setY(sinTheta);

    }

    public boolean backgroundEndX() {
        boolean flag;
        flag = !(backgroundX >= 0 && backgroundX <= displayWidth);
        if (character.getX() >= displayWidth / 2 && backgroundX < 0)
            backgroundX = 0;
        if (character.getX() <= displayWidth / 2 && backgroundX > displayWidth)
            backgroundX = displayWidth;
        return flag;
    }

    public boolean backgroundEndY() {
        boolean flag = !(backgroundY >= 0 && backgroundY <= displayHeight);
        if (character.getY() >= displayHeight / 2 && backgroundY < 0)
            backgroundY = 0;
        if (character.getY() <= displayHeight / 2 && backgroundY > displayHeight)
            backgroundY = displayHeight;
        return flag;
    }
}
