package com.nuaa.handwriting.model;

import java.util.List;

public class CheckIndentityResponse {

    private boolean validate;

    private String type;

    private List<String> list;

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
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
