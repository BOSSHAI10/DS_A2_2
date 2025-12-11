package com.example.monitoring.services;

import com.example.monitoring.dtos.MeasurementDTO;
import com.example.monitoring.entities.HourlyConsumption;
import com.example.monitoring.entities.Measurement;
import com.example.monitoring.repositories.HourlyConsumptionRepository;
import com.example.monitoring.repositories.MeasurementRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class MeasurementConsumer {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private HourlyConsumptionRepository hourlyConsumptionRepository;

    // Ascultă coada definită în RabbitMqConfig (asigură-te că numele cozii e corect)
    @Transactional
    @RabbitListener(queues = "device_queue")
    public void consumeMessage(MeasurementDTO dto) {
        try {
            System.out.println("Message received from device: " + dto.getDevice_id() + " | Value: " + dto.getMeasurement_value());

            // 1. Mapare DTO -> Entity și salvare măsurătoare brută
            Measurement measurement = new Measurement(
                    dto.getDevice_id(),
                    dto.getTimestamp(),
                    dto.getMeasurement_value()
            );
            measurementRepository.save(measurement);

            // 2. Calcul și actualizare consum orar
            processHourlyConsumption(dto);

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calculează totalul pe oră. Dacă există o înregistrare pentru ora respectivă,
     * adaugă valoarea nouă. Dacă nu, creează o înregistrare nouă.
     */
    private void processHourlyConsumption(MeasurementDTO dto) {
        // Conversie din timestamp (long) în LocalDateTime
        LocalDateTime date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dto.getTimestamp()),
                ZoneId.systemDefault()
        );

        // Trunchiem la oră fixă (ex: 14:25 devine 14:00)
        LocalDateTime currentHour = date.truncatedTo(ChronoUnit.HOURS);

        // Căutăm în baza de date dacă există deja o intrare pentru acest dispozitiv la această oră
        HourlyConsumption hourlyRecord = hourlyConsumptionRepository
                .findByDeviceIdAndHour(dto.getDevice_id(), currentHour)
                .orElse(new HourlyConsumption(
                        dto.getDevice_id(),
                        currentHour,
                        0.0
                ));

        // Adăugăm valoarea curentă la totalul existent
        double newTotal = hourlyRecord.getTotalConsumption() + dto.getMeasurement_value();
        hourlyRecord.setTotalConsumption(newTotal);

        // Salvăm (update sau insert)
        hourlyConsumptionRepository.save(hourlyRecord);

        System.out.println("Updated hourly consumption for Device " + dto.getDevice_id() +
                " at " + currentHour + ": " + newTotal + " kWh");
    }
}