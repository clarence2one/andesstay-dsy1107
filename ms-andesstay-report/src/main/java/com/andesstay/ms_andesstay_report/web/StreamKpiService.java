package com.andesstay.ms_andesstay_report.web;

import com.andesstay.ms_andesstay_report.config.ReservationKpiTopology;
import com.andesstay.ms_andesstay_report.messaging.ReservationKpi;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Interactive Queries (materia 3.1.2): lee el WindowStore que mantiene la topologia
 * de Streams y lo expone como API, sin consultar Oracle ni reiniciar el stream.
 */
@Service
public class StreamKpiService {

    private final KafkaStreamsInteractiveQueryService queryService;

    public StreamKpiService(KafkaStreamsInteractiveQueryService queryService) {
        this.queryService = queryService;
    }

    public List<ReservationKpi> kpis() {
        ReadOnlyWindowStore<String, Long> store;
        try {
            store = queryService.retrieveQueryableStore(
                    ReservationKpiTopology.KPI_STORE, QueryableStoreTypes.windowStore());
        } catch (IllegalStateException ex) {
            // Streams todavia esta arrancando/restaurando el estado.
            return List.of();
        }
        if (store == null) {
            return List.of();
        }

        List<ReservationKpi> result = new ArrayList<>();
        try (KeyValueIterator<Windowed<String>, Long> iterator = store.all()) {
            while (iterator.hasNext()) {
                KeyValue<Windowed<String>, Long> entry = iterator.next();
                result.add(new ReservationKpi(
                        entry.key.key(),
                        entry.key.window().start(),
                        entry.key.window().end(),
                        entry.value));
            }
        }
        result.sort(Comparator.comparingLong(ReservationKpi::getWindowStart).reversed());
        return result;
    }
}
