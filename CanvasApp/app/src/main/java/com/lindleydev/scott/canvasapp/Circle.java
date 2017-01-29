package com.lindleydev.scott.canvasapp;

/**
 * Created by Scott Lindley on 1/28/2017.
 */

public class Circle {

    private float mX;
    private float mY;
    private float radius = 1;
    private int[] mColor;

    public Circle(float x, float y, float radius, int[] color) {
        mX = x;
        mY = y;
        this.radius = radius;
        mColor = color;
    }

    public float getX() {
        return mX;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getY() {
        return mY;
    }

    public void setY(float y) {
        mY = y;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int[] getColor() {
        return mColor;
    }

    public void setColor(int[] color) {
        mColor = color;
    }
}
