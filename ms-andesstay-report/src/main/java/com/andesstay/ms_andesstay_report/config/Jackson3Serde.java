package com.andesstay.ms_andesstay_report.config;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Serde JSON para Kafka Streams usando el ObjectMapper Jackson 3 de Spring Boot 4.
 * Evita depender de JsonSerde (compilado contra Jackson 2) y mantiene el mismo contrato
 * JSON sin headers de tipo que ya usan el productor y los @KafkaListener del caso.
 */
public class Jackson3Serde<T> implements Serde<T> {

    private final Class<T> type;
    private final ObjectMapper objectMapper;

    public Jackson3Serde(Class<T> type, ObjectMapper objectMapper) {
        this.type = type;
        this.objectMapper = objectMapper;
    }

    @Override
    public Serializer<T> serializer() {
        return new Serializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public byte[] serialize(String topic, T data) {
                return data == null ? null : objectMapper.writeValueAsBytes(data);
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public Deserializer<T> deserializer() {
        return new Deserializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public T deserialize(String topic, byte[] data) {
                return data == null ? null : objectMapper.readValue(data, type);
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }
}
