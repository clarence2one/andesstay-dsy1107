package com.andesstay.ms_andesstay_report.messaging;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Kafka 4.1 no enlaza automaticamente un Validator a los @KafkaListener, asi que la
 * validacion jakarta de 3.3.3 se ejecuta explicitamente aqui. Si el payload no pasa, se lanza
 * una excepcion y el DefaultErrorHandler lo reintenta antes de enviarlo al DLT.
 */
@Component
public class PayloadValidator {

    private final Validator validator;

    public PayloadValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(Object payload) {
        Set<ConstraintViolation<Object>> violations = validator.validate(payload);
        if (!violations.isEmpty()) {
            String detail = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException("Payload de evento invalido: " + detail);
        }
    }
}
