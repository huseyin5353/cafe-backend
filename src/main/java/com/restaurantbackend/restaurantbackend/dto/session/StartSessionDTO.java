package com.restaurantbackend.restaurantbackend.dto.session;

public class StartSessionDTO {
    
    private String password;

    public StartSessionDTO() {}

    public StartSessionDTO(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}