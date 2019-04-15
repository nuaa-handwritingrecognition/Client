package com.nuaa.handwriting.model;

public class PullDataRequest {

    private String type;

    public PullDataRequest(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
