package com.restaurantbackend.restaurantbackend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    // IP bazlı rate limiting
    private final ConcurrentHashMap<String, ClientRequestInfo> clientRequests = new ConcurrentHashMap<>();
    
    // Rate limiting ayarları
    private static final int MAX_REQUESTS_PER_MINUTE = 100; // 3000 kullanıcı için uygun
    private static final int MAX_REQUESTS_PER_SECOND = 20;  // Burst koruması
    private static final long WINDOW_SIZE_MS = 60000; // 1 dakika
    private static final long BURST_WINDOW_MS = 1000; // 1 saniye

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = getClientIpAddress(request);
        String endpoint = request.getRequestURI();
        
        // Health check ve actuator endpoint'lerini bypass et
        if (endpoint.contains("/health") || endpoint.contains("/actuator")) {
            return true;
        }
        
        ClientRequestInfo clientInfo = clientRequests.computeIfAbsent(clientIp, k -> new ClientRequestInfo());
        
        long currentTime = System.currentTimeMillis();
        
        // Burst protection (saniye bazlı)
        if (!isBurstAllowed(clientInfo, currentTime)) {
            sendRateLimitResponse(response, "Too many requests per second", 429);
            return false;
        }
        
        // Rate limiting (dakika bazlı)
        if (!isRateLimitAllowed(clientInfo, currentTime)) {
            sendRateLimitResponse(response, "Rate limit exceeded. Try again later.", 429);
            return false;
        }
        
        // Request'i kaydet
        clientInfo.recordRequest(currentTime);
        
        return true;
    }
    
    private boolean isBurstAllowed(ClientRequestInfo clientInfo, long currentTime) {
        // Son 1 saniyedeki request sayısını kontrol et
        clientInfo.cleanOldBurstRequests(currentTime);
        return clientInfo.getBurstRequestCount() < MAX_REQUESTS_PER_SECOND;
    }
    
    private boolean isRateLimitAllowed(ClientRequestInfo clientInfo, long currentTime) {
        // Son 1 dakikadaki request sayısını kontrol et
        clientInfo.cleanOldRequests(currentTime);
        return clientInfo.getRequestCount() < MAX_REQUESTS_PER_MINUTE;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private void sendRateLimitResponse(HttpServletResponse response, String message, int statusCode) {
        try {
            response.setStatus(statusCode);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"" + message + "\",\"status\":" + statusCode + "}");
        } catch (Exception e) {
            // Log error but don't throw
            System.err.println("Error sending rate limit response: " + e.getMessage());
        }
    }
    
    // Client request bilgilerini tutan inner class
    private static class ClientRequestInfo {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private final AtomicInteger burstRequestCount = new AtomicInteger(0);
        private final AtomicLong lastCleanup = new AtomicLong(System.currentTimeMillis());
        private final AtomicLong lastBurstCleanup = new AtomicLong(System.currentTimeMillis());
        
        public void recordRequest(long currentTime) {
            requestCount.incrementAndGet();
            burstRequestCount.incrementAndGet();
        }
        
        public int getRequestCount() {
            return requestCount.get();
        }
        
        public int getBurstRequestCount() {
            return burstRequestCount.get();
        }
        
        public void cleanOldRequests(long currentTime) {
            if (currentTime - lastCleanup.get() > WINDOW_SIZE_MS) {
                requestCount.set(0);
                lastCleanup.set(currentTime);
            }
        }
        
        public void cleanOldBurstRequests(long currentTime) {
            if (currentTime - lastBurstCleanup.get() > BURST_WINDOW_MS) {
                burstRequestCount.set(0);
                lastBurstCleanup.set(currentTime);
            }
        }
    }
}
