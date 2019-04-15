package com.nuaa.handwriting.model;

public class PointBean {

    private float x;

    private float y;

    private float press;

    public PointBean(float x, float y, float press) {
        this.x = x;
        this.y = y;
        this.press = press;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getPress() {
        return press;
    }

    public void setPress(float press) {
        this.press = press;
    }
}
