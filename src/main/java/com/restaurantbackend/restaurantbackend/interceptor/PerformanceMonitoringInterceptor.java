package com.restaurantbackend.restaurantbackend.interceptor;

import com.restaurantbackend.restaurantbackend.service.monitoring.PerformanceMonitoringService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PerformanceMonitoringInterceptor implements HandlerInterceptor {

    @Autowired
    private PerformanceMonitoringService monitoringService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        
        // Health check endpoint'lerini izleme
        String requestURI = request.getRequestURI();
        if (!requestURI.contains("/health") && !requestURI.contains("/actuator")) {
            monitoringService.incrementActiveConnections();
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime != null) {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            // Health check endpoint'lerini izleme
            String requestURI = request.getRequestURI();
            if (!requestURI.contains("/health") && !requestURI.contains("/actuator")) {
                boolean success = response.getStatus() >= 200 && response.getStatus() < 400;
                monitoringService.recordRequest(responseTime, success);
                monitoringService.decrementActiveConnections();
            }
        }
    }
}
