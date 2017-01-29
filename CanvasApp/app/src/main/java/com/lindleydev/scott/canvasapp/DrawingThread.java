package com.lindleydev.scott.canvasapp;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Scott on 1/28/17.
 */
public class DrawingThread extends Thread implements SensorEventListener{
    private DrawingView mView;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private int[] mXYZ;
    private boolean mRun;

    public DrawingThread(DrawingView view) {
        mView = view;
        mRun = true;
    }

    @Override
    public void run() {
        super.run();
        if (mView.getSimNumber() == 1) {
            startSim1Loop();
        } else {
            mXYZ = new int[3];
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
                mView.drawCircle(c.getX(), c.getY(), c.getRadius());
            }
            mView.commitDrawing();
        }
        mSensorManager.unregisterListener(this);
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

        float prevX = mXYZ[0];
        float prevY = mXYZ[1];
        float prevZ = mXYZ[2];

        if (y > 0){
            //move it down
        }
        if (y < 0){
            //move it up
        }

        Log.d(TAG, "x,y,z =  "+x +", "+y +", "+z);
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
