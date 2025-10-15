package com.restaurantbackend.restaurantbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.restaurantbackend.restaurantbackend.interceptor.RateLimitingInterceptor;
import com.restaurantbackend.restaurantbackend.interceptor.PerformanceMonitoringInterceptor;

@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {

    @Bean
    public RateLimitingInterceptor rateLimitingInterceptor() {
        return new RateLimitingInterceptor();
    }

    @Bean
    public PerformanceMonitoringInterceptor performanceMonitoringInterceptor() {
        return new PerformanceMonitoringInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health", "/api/actuator/**", "/api/monitoring/**");
        
        registry.addInterceptor(performanceMonitoringInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health", "/api/actuator/**", "/api/monitoring/**");
    }
}
