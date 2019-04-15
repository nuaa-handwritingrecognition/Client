package com.nuaa.handwriting.model;

import java.util.List;

public class MergeInfoRequest {

    private String userName;

    private String phone;

    private String type;

    private List<String> list;

    public MergeInfoRequest(String userName, String phone, String type, List<String> list) {
        this.userName = userName;
        this.phone = phone;
        this.type = type;
        this.list = list;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
