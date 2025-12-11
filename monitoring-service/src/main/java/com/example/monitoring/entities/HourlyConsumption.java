package com.example.monitoring.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class HourlyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID deviceId;

    // Vom stoca ora exactă (ex: 2024-03-20 10:00:00)
    @Column(nullable = false)
    private LocalDateTime hour;

    @Column(nullable = false)
    private double totalConsumption;

    public HourlyConsumption() {
    }

    public HourlyConsumption(UUID deviceId, LocalDateTime hour, double totalConsumption) {
        this.deviceId = deviceId;
        this.hour = hour;
        this.totalConsumption = totalConsumption;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }
    public LocalDateTime getHour() { return hour; }
    public void setHour(LocalDateTime hour) { this.hour = hour; }
    public double getTotalConsumption() { return totalConsumption; }
    public void setTotalConsumption(double totalConsumption) { this.totalConsumption = totalConsumption; }
}