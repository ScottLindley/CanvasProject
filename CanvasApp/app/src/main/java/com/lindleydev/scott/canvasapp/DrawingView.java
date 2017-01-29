package com.lindleydev.scott.canvasapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Scott on 1/28/17.
 */
public class DrawingView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawingThread mThread;
    private Paint mPaint;
    private Canvas mCanvas;
    private List<Pointer> mPointers;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        mPointers = new ArrayList<>();
        mPaint = new Paint();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mThread = new DrawingThread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    public void beginDrawing(){
        mCanvas = getHolder().lockCanvas();
        mCanvas.drawColor(Color.WHITE);
    }

    public void drawCircle(float x, float y, float rad){
        mCanvas.drawCircle(x, y, rad, mPaint);
    }

    public void commitDrawing(){
        getHolder().unlockCanvasAndPost(mCanvas);
        mCanvas = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int id = event.getPointerId(index);
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mPointers.add(new Pointer(id,
                        event.getX(event.findPointerIndex(id)),
                        event.getY(event.findPointerIndex(id)),
                        getRandomPaintColor()));
                Collections.sort(mPointers);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mPointers.remove(mPointers.get(event.findPointerIndex(id)));
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                for (int i=0; i<pointerCount; i++){
                    int pointerID = event.getPointerId(i);
                    mPointers.get(event.findPointerIndex(pointerID))
                            .setX(event.getX(event.findPointerIndex(pointerID)));
                    mPointers.get(event.findPointerIndex(pointerID))
                            .setY(event.getY(event.findPointerIndex(pointerID)));
                }
                break;
        }
        return true;
    }

    public List<Pointer> getPointers() {
        return mPointers;
    }

    public int[] getRandomPaintColor(){
        int red = (int) (Math.random() * 256);
        int green = (int) (Math.random() * 256);
        int blue = (int) (Math.random() * 256);
        return new int[]{red, green, blue};
    }

    public Paint getPaint() {
        return mPaint;
    }

    public DrawingThread getThread() {
        return mThread;
    }
}