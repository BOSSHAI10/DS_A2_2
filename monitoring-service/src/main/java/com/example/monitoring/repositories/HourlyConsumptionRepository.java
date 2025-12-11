package com.example.monitoring.repositories;

import com.example.monitoring.entities.HourlyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface HourlyConsumptionRepository extends JpaRepository<HourlyConsumption, UUID> {
    // Căutăm dacă există deja o înregistrare pentru dispozitivul X la ora Y
    Optional<HourlyConsumption> findByDeviceIdAndHour(UUID deviceId, LocalDateTime hour);
}