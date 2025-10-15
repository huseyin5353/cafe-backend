package com.restaurantbackend.restaurantbackend.util;

import java.util.Random;

public class PasswordGenerator {


    public String generateNumericPassword() {
        Random random = new Random();
        int number = random.nextInt(9000) + 1000;
        return String.valueOf(number);
    }
}