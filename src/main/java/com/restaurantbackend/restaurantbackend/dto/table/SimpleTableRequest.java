package com.restaurantbackend.restaurantbackend.dto.table;

public class SimpleTableRequest {
    private String location;

    public SimpleTableRequest() {}

    public SimpleTableRequest(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}




