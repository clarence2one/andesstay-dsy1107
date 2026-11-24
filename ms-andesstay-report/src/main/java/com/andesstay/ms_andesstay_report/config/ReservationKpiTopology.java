package com.andesstay.ms_andesstay_report.config;

import com.andesstay.ms_andesstay_report.messaging.ReservationEvent;
import com.andesstay.ms_andesstay_report.messaging.ReservationKpi;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.EmitStrategy;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

/**
 * Topologia real de Kafka Streams (materia 3.1.2 y 3.2.2), adaptada al caso AndesStay:
 *
 *   reservations.events -> filter -> selectKey(unitId) -> windowedBy(1 min)
 *                       -> count() -> state store -> reservations.kpis
 *
 * El estado local se consulta en caliente con Interactive Queries desde
 * GET /api/report/stream-kpis, sin detener la aplicacion ni ir a la base de datos.
 */
@Configuration
@EnableKafkaStreams
public class ReservationKpiTopology {

    public static final String KPI_STORE = "reservation-kpi-window-store";
    public static final String KPI_TOPIC = "reservations.kpis";

    private final ObjectMapper objectMapper;

    public ReservationKpiTopology(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public KStream<String, ReservationEvent> reservationKpiStream(StreamsBuilder builder,
                                                                  KafkaProperties kafkaProperties) {
        Jackson3Serde<ReservationEvent> eventSerde = new Jackson3Serde<>(ReservationEvent.class, objectMapper);
        Jackson3Serde<ReservationKpi> kpiSerde = new Jackson3Serde<>(ReservationKpi.class, objectMapper);

        KStream<String, ReservationEvent> events = builder.stream(
                kafkaProperties.topicReservationsEvents(),
                Consumed.with(Serdes.String(), eventSerde));

        events
                // 1) Filtro: solo estados de reserva validos para el KPI.
                .filter((reservationId, event) -> event != null && event.newStatus() != null
                                && !event.newStatus().isBlank(),
                        Named.as("filter-valid-reservation-events"))
                // 2) Re-key: agrupamos por unidad (antes el key era reservationId).
                .selectKey((reservationId, event) -> String.valueOf(event.unitId()),
                        Named.as("key-by-unit"))
                // 3) Ventana de 1 minuto + 4) agregacion con state store local.
                // Gracia de 24 h solo para conservar el estado; emision en vivo se mantiene
                // con onWindowUpdate() y el evento sigue cayendo en su ventana de 1 minuto.
                .groupByKey(Grouped.with(Serdes.String(), eventSerde))
                .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMinutes(1), Duration.ofDays(1)))
                // Emitir en cada actualizacion: el KPI se ve en reservations.kpis al instante,
                // sin esperar a que cierre la ventana de 1 minuto (comportamiento default).
                .emitStrategy(EmitStrategy.onWindowUpdate())
                .count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as(KPI_STORE)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long()))
                // 5) Publicacion del resultado en reservations.kpis (key = unitId).
                .toStream()
                .map((windowedUnit, count) -> KeyValue.pair(
                                windowedUnit.key(),
                                new ReservationKpi(windowedUnit.key(),
                                        windowedUnit.window().start(),
                                        windowedUnit.window().end(),
                                        count)),
                        Named.as("kpi-to-unit-key"))
                .to(KPI_TOPIC, Produced.with(Serdes.String(), kpiSerde));

        return events;
    }

    /** Consulta interactiva del WindowStore: permite mostrar el estado sin parar Streams. */
    @Bean
    public KafkaStreamsInteractiveQueryService kafkaStreamsInteractiveQueryService(
            StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
        return new KafkaStreamsInteractiveQueryService(streamsBuilderFactoryBean);
    }
}
