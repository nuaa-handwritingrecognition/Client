package com.nuaa.handwriting.model;

import java.util.List;

public class AddListRequest {

    private String userName;

    private String phone;

    private String actValue;

    private boolean sys;

    private List<VectorBean> list;

    public AddListRequest(String userName, String phone, String actValue, boolean sys, List<VectorBean> list) {
        this.userName = userName;
        this.phone = phone;
        this.actValue = actValue;
        this.sys = sys;
        this.list = list;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<VectorBean> getList() {
        return list;
    }

    public void setList(List<VectorBean> list) {
        this.list = list;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActValue() {
        return actValue;
    }

    public void setActValue(String actValue) {
        this.actValue = actValue;
    }

    public boolean isSys() {
        return sys;
    }

    public void setSys(boolean sys) {
        this.sys = sys;
    }
}
