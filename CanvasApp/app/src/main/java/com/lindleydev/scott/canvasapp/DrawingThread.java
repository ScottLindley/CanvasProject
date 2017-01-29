package com.lindleydev.scott.canvasapp;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by Scott on 1/28/17.
 */
public class DrawingThread extends Thread implements SensorEventListener{
    private DrawingView mView;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private float[] mXYZ;
    private int mViewWidth;
    private int mViewHeight;
    private boolean mRun;

    public DrawingThread(DrawingView view) {
        mView = view;
        mViewWidth = view.getWidth();
        mViewHeight = view.getHeight();
        mRun = true;
    }

    @Override
    public void run() {
        super.run();
        if (mView.getSimNumber() == 1) {
            startSim1Loop();
        } else {
            mXYZ = new float[3];
            setUpAccelerometer();
            startSim2Loop();
        }
    }

    private void startSim1Loop(){
        while(mRun){
            mView.beginDrawing();
            List<Pointer> pointers = mView.getPointers();
            for (int i=0; i<pointers.size(); i++){
                try {
                    Pointer p = pointers.get(i);
                    int[] color = p.getColor();
                    mView.getPaint().setColor(Color.argb(130, color[0], color[1], color[2]));
                    mView.drawCircle(p.getX(), p.getY(), p.getRadius());
                    p.setRadius(p.getRadius()+3);
                } catch (ArrayIndexOutOfBoundsException e){

                }
            }
            mView.commitDrawing();
        }
    }

    private void startSim2Loop(){
        while(mRun){
            mView.beginDrawing();
            List<Pointer> pointers = mView.getPointers();
            for (int i=0; i<pointers.size(); i++){
                try {
                    Pointer p = pointers.get(i);
                    int[] color = p.getColor();
                    mView.getPaint().setColor(Color.argb(130, color[0], color[1], color[2]));
                    mView.drawCircle(p.getX(), p.getY(), p.getRadius());
                    p.setRadius(p.getRadius()+3);
                } catch (ArrayIndexOutOfBoundsException e){

                }
            }
            List<Circle> circles = mView.getCircles();
            for (int i=0; i<circles.size(); i++){
                Circle c = circles.get(i);
                int[] color = c.getColor();
                mView.getPaint().setColor(Color.argb(130, color[0], color[1], color[2]));
                if (c.getRadius() > 0) {
                    mView.drawCircle(c.getX(), c.getY(), c.getRadius());
                }
                double baseSpeed = c.getFallSpeedFactor();
                setNewXY(c, (float) baseSpeed);

            }
            mView.commitDrawing();
        }
        mSensorManager.unregisterListener(this);
    }


    private void setNewXY(Circle c, float baseSpeed) {
        float speedX = baseSpeed * mXYZ[0] / 10;
        float speedY = baseSpeed * mXYZ[1] / 10;

        boolean atTop = c.getY()-c.getRadius() <= 0;
        boolean atBottom = c.getY()+c.getRadius() >= mViewHeight;
        boolean atRight = c.getX()+c.getRadius() >= mViewWidth;
        boolean atLeft = c.getX()-c.getRadius() <= 0;

        if (!atRight && speedX < 0) {
            c.setX(c.getX() - speedX);
        }
        if (!atLeft && speedX > 0) {
            c.setX(c.getX() - speedX);
        }
        if (!atTop && speedY < 0){
            c.setY(c.getY() + speedY);
        }
        if (!atBottom && speedY > 0){
            c.setY(c.getY() + speedY);
        }
    }


    private void setUpAccelerometer(){
        mSensorManager =
                (SensorManager) mView.getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        mXYZ[0] = x;
        mXYZ[1] = y;
        mXYZ[2] = z;

//        if (y > 0){
//            //move it down
//            for (Circle c : mView.getCircles()){
//                c.setY(c.getY()-);
//            }
//        }
//        if (y < 0){
//            //move it up
//        }

//        Log.d(TAG, "x,y,z =  "+x +", "+y +", "+z);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public boolean isRunning() {
        return mRun;
    }

    public void setRunning(boolean mRun) {
        this.mRun = mRun;
    }

}
