package com.example.shasank.canvasdraw;

import android.graphics.Canvas;
import android.view.SurfaceHolder;


class ControlThread extends Thread {
    private MySurfaceView gameSurface;
    private SurfaceHolder surfaceHolder;
    private boolean runningStatus;

    ControlThread(MySurfaceView gameSurface, SurfaceHolder surfaceHolder) {
        this.gameSurface = gameSurface;
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        do {
            long now = System.nanoTime();
            long waitTime = (now - startTime) / 100000;
            Canvas canvas = null;
            try {
                // Get Canvas from Holder and lock it.
                canvas = this.surfaceHolder.lockCanvas();

                // Synchronized
                synchronized (canvas) {
                    this.gameSurface.update(waitTime);
                    this.gameSurface.draw(canvas);

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    // Unlock Canvas.
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            if (waitTime > 200)
                startTime = System.nanoTime();

        } while (runningStatus);
    }

    void setRunningStatus(boolean status) {
        runningStatus = status;
    }

    boolean getRunningStatus() {
        return runningStatus;
    }
}
