package com.restaurantbackend.restaurantbackend.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @GetMapping("/ping")
    public Map<String, String> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Backend is running!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }
    
    @GetMapping("/departments-simple")
    public Map<String, Object> departmentsSimple() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Departments test endpoint");
        response.put("data", new Object[]{
            Map.of("id", 1, "name", "Mutfak", "active", true),
            Map.of("id", 2, "name", "Bar", "active", true),
            Map.of("id", 3, "name", "Tatlı", "active", true)
        });
        return response;
    }
}