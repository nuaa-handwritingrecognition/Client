package com.nuaa.handwriting.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VectorBean implements Serializable {

    @SerializedName("vertx")
    private float vectorX;

    @SerializedName("verty")
    private float vectorY;

    private float press;

    public VectorBean(float vectorX, float vectorY, float press) {
        this.vectorX = vectorX;
        this.vectorY = vectorY;
        this.press = press;
    }

    public float getVectorX() {
        return vectorX;
    }

    public void setVectorX(float vectorX) {
        this.vectorX = vectorX;
    }

    public float getVectorY() {
        return vectorY;
    }

    public void setVectorY(float vectorY) {
        this.vectorY = vectorY;
    }

    public float getPress() {
        return press;
    }

    public void setPress(float press) {
        this.press = press;
    }
}
