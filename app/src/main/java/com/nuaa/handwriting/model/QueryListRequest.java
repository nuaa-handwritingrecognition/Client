package com.nuaa.handwriting.model;

public class QueryListRequest {

    private String userName;

    private String phone;

    public QueryListRequest(String userName, String phone) {
        this.userName = userName;
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
