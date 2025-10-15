package com.restaurantbackend.restaurantbackend.service.monitoring;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PerformanceMonitoringService {

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong maxResponseTime = new AtomicLong(0);
    private final AtomicLong minResponseTime = new AtomicLong(Long.MAX_VALUE);

    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    public void recordRequest(long responseTime, boolean success) {
        totalRequests.incrementAndGet();
        
        if (success) {
            successfulRequests.incrementAndGet();
        } else {
            failedRequests.incrementAndGet();
        }
        
        totalResponseTime.addAndGet(responseTime);
        
        // Max response time
        long currentMax = maxResponseTime.get();
        while (responseTime > currentMax && !maxResponseTime.compareAndSet(currentMax, responseTime)) {
            currentMax = maxResponseTime.get();
        }
        
        // Min response time
        long currentMin = minResponseTime.get();
        while (responseTime < currentMin && !minResponseTime.compareAndSet(currentMin, responseTime)) {
            currentMin = minResponseTime.get();
        }
    }

    public void incrementActiveConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementActiveConnections() {
        activeConnections.decrementAndGet();
    }

    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Request metrics
        long total = totalRequests.get();
        long successful = successfulRequests.get();
        long failed = failedRequests.get();
        
        metrics.put("requests", Map.of(
            "total", total,
            "successful", successful,
            "failed", failed,
            "successRate", total > 0 ? (double) successful / total * 100 : 0.0
        ));
        
        // Response time metrics
        if (total > 0) {
            metrics.put("responseTime", Map.of(
                "average", totalResponseTime.get() / total,
                "max", maxResponseTime.get(),
                "min", minResponseTime.get() == Long.MAX_VALUE ? 0 : minResponseTime.get()
            ));
        }
        
        // Connection metrics
        metrics.put("connections", Map.of(
            "active", activeConnections.get()
        ));
        
        // Memory metrics
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        long freeMemory = maxMemory - usedMemory;
        
        metrics.put("memory", Map.of(
            "used", usedMemory,
            "max", maxMemory,
            "free", freeMemory,
            "usagePercentage", maxMemory > 0 ? (double) usedMemory / maxMemory * 100 : 0.0
        ));
        
        // System metrics
        metrics.put("system", Map.of(
            "cpuUsage", getCpuUsage(),
            "availableProcessors", osBean.getAvailableProcessors(),
            "threadCount", threadBean.getThreadCount(),
            "peakThreadCount", threadBean.getPeakThreadCount()
        ));
        
        return metrics;
    }

    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        
        // Memory health
        double memoryUsage = getMemoryUsagePercentage();
        String memoryStatus = memoryUsage > 90 ? "CRITICAL" : 
                             memoryUsage > 80 ? "WARNING" : "HEALTHY";
        
        // CPU health
        double cpuUsage = getCpuUsage();
        String cpuStatus = cpuUsage > 90 ? "CRITICAL" : 
                          cpuUsage > 80 ? "WARNING" : "HEALTHY";
        
        // Request success rate
        long total = totalRequests.get();
        long successful = successfulRequests.get();
        double successRate = total > 0 ? (double) successful / total * 100 : 100.0;
        String requestStatus = successRate < 95 ? "CRITICAL" : 
                              successRate < 98 ? "WARNING" : "HEALTHY";
        
        // Overall health
        String overallStatus = "HEALTHY";
        if (memoryStatus.equals("CRITICAL") || cpuStatus.equals("CRITICAL") || requestStatus.equals("CRITICAL")) {
            overallStatus = "CRITICAL";
        } else if (memoryStatus.equals("WARNING") || cpuStatus.equals("WARNING") || requestStatus.equals("WARNING")) {
            overallStatus = "WARNING";
        }
        
        health.put("status", overallStatus);
        health.put("components", Map.of(
            "memory", Map.of("status", memoryStatus, "usage", memoryUsage),
            "cpu", Map.of("status", cpuStatus, "usage", cpuUsage),
            "requests", Map.of("status", requestStatus, "successRate", successRate)
        ));
        
        return health;
    }

    private double getMemoryUsagePercentage() {
        long used = memoryBean.getHeapMemoryUsage().getUsed();
        long max = memoryBean.getHeapMemoryUsage().getMax();
        return max > 0 ? (double) used / max * 100 : 0.0;
    }

    private double getCpuUsage() {
        try {
            // OperatingSystemMXBean'den CPU usage almak için cast gerekli
            com.sun.management.OperatingSystemMXBean sunOsBean = 
                (com.sun.management.OperatingSystemMXBean) osBean;
            return sunOsBean.getProcessCpuLoad() * 100;
        } catch (Exception e) {
            // Fallback: System load average kullan
            return osBean.getSystemLoadAverage() * 100;
        }
    }

    public void resetMetrics() {
        totalRequests.set(0);
        successfulRequests.set(0);
        failedRequests.set(0);
        totalResponseTime.set(0);
        maxResponseTime.set(0);
        minResponseTime.set(Long.MAX_VALUE);
    }
}
