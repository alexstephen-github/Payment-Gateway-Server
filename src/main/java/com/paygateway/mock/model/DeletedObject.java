package com.paygateway.mock.model;

public class DeletedObject {
    public String id;
    public String object;
    public Boolean deleted = true;

    public DeletedObject() {
    }

    public DeletedObject(String id, String object) {
        this.id = id;
        this.object = object;
    }
}
