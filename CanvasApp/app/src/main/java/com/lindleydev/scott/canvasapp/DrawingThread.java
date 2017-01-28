package com.lindleydev.scott.canvasapp;

import android.graphics.Color;

import java.util.List;

/**
 * Created by Scott on 1/28/17.
 */
public class DrawingThread extends Thread{
    private DrawingView mView;
    private boolean mRun;

    public DrawingThread(DrawingView view) {
        mView = view;
        mRun = true;
    }

    @Override
    public void run() {
        super.run();
        startLoop();
    }

    private void startLoop(){
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

    public boolean isRunning() {
        return mRun;
    }

    public void setRunning(boolean mRun) {
        this.mRun = mRun;
    }

}
