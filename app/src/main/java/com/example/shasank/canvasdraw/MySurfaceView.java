package com.example.shasank.canvasdraw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    Bitmap controller,background;
    private final int movedX, movedY;
    int startX = 1, startY = 1;
    int top, left;
    int bTop,bLeft,bRight,bBottom;
    Rect gameController,controllerImage,gameRect,backReact;
    int controlAngle = 200;
    ControlThread controlThread;
    Character character;

    public MySurfaceView(Context context) {
        super(context);
        left = Resources.getSystem().getDisplayMetrics().widthPixels;
        top = Resources.getSystem().getDisplayMetrics().heightPixels;
        movedX = left * 3 / 100;
        movedY = top * 3 / 100;
        this.setFocusable(true);
        this.getHolder().addCallback(this);
        controller = BitmapFactory.decodeResource(getResources(), R.drawable.inactive);
        background= BitmapFactory.decodeResource(getResources(),R.drawable.back);
        character = new Character(context, top, left, 0);
        controlThread = new ControlThread(this, this.getHolder());
        controlThread.setRunningStatus(false);
        controllerImage = new Rect(0, 0, controller.getWidth(), controller.getHeight());
        int l = left * 5 / 100,
                r = left * 15 / 100,
                t = top * 75 / 100,
                b = top * 9 / 10;
        gameController = new Rect(l, t, r, b);
        gameRect=new Rect(0,0,left,top);
        backReact=new Rect(0,0,background.getWidth(),background.getHeight());
        bTop=0;
        bLeft=0;
        bRight=background.getWidth();
        bBottom=background.getHeight();
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
        canvas.drawBitmap(background,backReact,gameRect,null);
        character.draw(canvas);
        canvas.drawBitmap(controller, controllerImage, gameController, null);
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
                    if (Math.abs(startX - x) > movedX || Math.abs(startY - y) > movedY) {
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
        manageBackgroundRect();
        character.update(waitTime);
    }

    public void manageBackgroundRect(){
        if(controlThread.getRunningStatus())
        background=Bitmap.createBitmap(background,bLeft+50,bTop+50,bRight+50,bBottom+50);
    }
}
