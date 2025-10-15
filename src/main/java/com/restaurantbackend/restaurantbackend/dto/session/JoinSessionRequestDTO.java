package com.restaurantbackend.restaurantbackend.dto.session;

public class JoinSessionRequestDTO {
    
    private String pin;
    private String customerName;
    private String deviceId;

    public JoinSessionRequestDTO() {}

    public JoinSessionRequestDTO(String pin, String customerName, String deviceId) {
        this.pin = pin;
        this.customerName = customerName;
        this.deviceId = deviceId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}











