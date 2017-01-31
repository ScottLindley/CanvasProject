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
    private static final int SHAKE_THRESHOLD = 900;
    private DrawingView mView;
    private Sensor mAccelSensor;
    private SensorManager mSensorManager;
    private long mCurrentTime;
    private long mLastTime;
    private float[] mXYZ;
    private float[] mLastXYZ;
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
        mXYZ = new float[3];
        mLastXYZ = new float[3];
        setUpAccelerometer();
        if (mView.getSimNumber() == 1) {
            startSim1Loop();
        } else {
            startSim2Loop();
        }
    }

    private void startSim1Loop(){
        while(mRun){
            mView.beginDrawing();
            List<Circle> circles = mView.getCircles();
            for (Circle c : circles) {
                calculateNewVelocity(c);
                checkForBorderContact(c, calculateNewPosition(c));
                int[] color = c.getColor();
                mView.getPaint().setColor(Color.argb(255, color[0], color[1], color[2]));
                mView.drawCircle(c.getX(), c.getY(), c.getRadius());
            }
            mView.commitDrawing();
        }
        mSensorManager.unregisterListener(this);
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

    private void calculateNewVelocity(Circle c){
        mCurrentTime = System.currentTimeMillis();
        float[] lastVelocity = c.getVelocity();
        float vX0 = lastVelocity[0];
        float vY0 = lastVelocity[1];

        long timeDiff = mCurrentTime - mLastTime;

        float newVelocityX = vX0 + (mXYZ[0]*timeDiff)/100;
        float newVelocityY = vY0 + (mXYZ[1]*timeDiff)/100;

        c.setVelocity(new float[]{newVelocityX, newVelocityY});
    }


    private float[] calculateNewPosition(Circle c){
        float x0 = c.getX();
        float y0 = c.getY();

        // x = x0 + v0t + 1/2at^2
        float timeDiff = (mCurrentTime - mLastTime);
        timeDiff /= 3.8;
//        Log.d(TAG, "Time = "+timeDiff);
        float[] position = new float[2];
//        Log.d(TAG, "Velocity = "+c.getVelocity()[0]+", "+c.getVelocity()[1]);
//        Log.d(TAG, "Acceleration = " +mXYZ[0]+", "+mXYZ[1]);
        position[0] = (x0 + (c.getVelocity()[0]*timeDiff) + ((1/2)*mXYZ[0]*timeDiff*timeDiff)
                        + ((1/6)*mLastXYZ[0]*timeDiff*timeDiff*timeDiff));
        position[1] = (y0 + (c.getVelocity()[1]*timeDiff) + ((1/2)*mXYZ[1]*timeDiff*timeDiff)
                + ((1/6)*mLastXYZ[1]*timeDiff*timeDiff*timeDiff));


//        Log.d(TAG, "Position = "+position[0]+", "+position[1]);
        mLastTime = mCurrentTime;
        return position;
    }

    private void checkForBorderContact(Circle c, float[] position){
        boolean[] borders = checkForBorders(c, position);
        if(c.getVelocity()[0] < 0) {
            if (!borders[0]){
                //not at left border with negative x velocity
                c.setX(position[0]);
            } else {
                float vCollision = (float) (c.getVelocity()[0]*(-0.62));
                c.setVelocity(new float[]{vCollision, c.getVelocity()[1]});
            }
        }
        if (c.getVelocity()[1] < 0) {
            if (!borders[1]){
                Log.d(TAG, "NOT AT TOP");
                //not at top border with negative y velocity
                c.setY(position[1]);
            } else {
                Log.d(TAG, "AT TOP");
                float vCollision = (float) (c.getVelocity()[1]*(-0.62));
                c.setVelocity(new float[]{c.getVelocity()[0], vCollision});
            }
        }
        if (c.getVelocity()[0] > 0){
            if (!borders[2]) {
                //not at right border with positive x velocity
                if (!borders[0]) {
                    c.setX(position[0]);
                }
            } else {
                float vCollision = (float) (c.getVelocity()[0]*(-0.62));
                c.setVelocity(new float[]{vCollision, c.getVelocity()[1]});
            }
        }
        if (c.getVelocity()[1] > 0){
            if (!borders[3]){
                //not at bottom border with positive y velocity
                if (!borders[1]) {
                    c.setY(position[1]);
                }
            } else {
                float vCollision = (float) (c.getVelocity()[1]*(-0.62));
                c.setVelocity(new float[]{c.getVelocity()[0], vCollision});
            }
        }
//        Log.d(TAG, "velocity = "+c.getVelocity()[1]);
    }

    private boolean[] checkForBorders(Circle c, float[] position){
        boolean[] borders = new boolean[4];
        borders[0] = position[0]-c.getRadius() <= 0;
        borders[1] = position[1]-c.getRadius() <= 0;
        borders[2] = position[0]+c.getRadius() >= mViewWidth;
        borders[3] = position[1]+c.getRadius() >= mViewHeight;
        return borders;
    }



    private void setUpAccelerometer(){
        mSensorManager =
                (SensorManager) mView.getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] values = sensorEvent.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

//            Log.d(TAG, x + ", " + y + ", " + z);

            if (mView.getSimNumber() == 2) {
                mCurrentTime = System.currentTimeMillis();
                long timeDiff = mCurrentTime - mLastTime;

                float lastX = mXYZ[0];
                float lastY = mXYZ[1];
                float lastZ = mXYZ[2];


                float phoneSpeed = Math.abs(x + y + z - lastX - lastY - lastZ) / timeDiff * 1000;
                if (phoneSpeed > SHAKE_THRESHOLD) {
                    Log.d(TAG, "onSensorChanged: " + phoneSpeed);
                    mView.getCircles().removeAll(mView.getCircles());
                }

                mLastTime = mCurrentTime;
            }
            mLastXYZ[0] = mXYZ[0];
            mLastXYZ[1] = mXYZ[1];
            mLastXYZ[2] = mXYZ[2];

            if (mView.getSimNumber() == 1){
                mXYZ[0] = x * -1f;
            } else {
                mXYZ[0] = x;
            }
            mXYZ[1] = y;
            mXYZ[2] = z;
        }
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
