package com.andesstay.ms_andesstay_report.messaging;

import com.andesstay.ms_andesstay_report.domain.ReservationEventFact;
import com.andesstay.ms_andesstay_report.repository.ReservationEventFactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ReservationEventListener {

    private static final Logger log = LoggerFactory.getLogger(ReservationEventListener.class);

    private final ReservationEventFactRepository repository;
    private final PayloadValidator payloadValidator;

    public ReservationEventListener(ReservationEventFactRepository repository, PayloadValidator payloadValidator) {
        this.repository = repository;
        this.payloadValidator = payloadValidator;
    }

    @KafkaListener(topics = "${andesstay.kafka.topic-reservations-events}")
    public void onReservationEvent(@Payload ReservationEvent event) {
        payloadValidator.validate(event);
        // Correlation ID del productor propagado al log del consumidor (3.3.3).
        MDC.put("traceId", event.traceId());
        MDC.put("correlationId", event.correlationId());
        try {
            if (repository.existsByEventId(event.eventId())) {
                log.info("Evento {} ya estaba en las metricas, se ignora el redelivery", event.eventId());
                return;
            }

            ReservationEventFact fact = ReservationEventFact.builder()
                    .eventId(event.eventId())
                    .reservationId(event.reservationId())
                    .unitId(event.unitId())
                    .previousStatus(event.previousStatus())
                    .newStatus(event.newStatus())
                    .occurredAt(event.timestamp())
                    .build();
            repository.save(fact);
            log.info("Metrica registrada: reserva {} -> {}", event.reservationId(), event.newStatus());
        } finally {
            MDC.clear();
        }
    }
}
