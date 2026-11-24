package com.andesstay.ms_andesstay_report.web;

import com.andesstay.ms_andesstay_report.messaging.ReservationKpi;
import com.andesstay.ms_andesstay_report.web.dto.KpisResponse;
import com.andesstay.ms_andesstay_report.web.dto.TopUnitResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** API de solo lectura para Admin: reservas por hora, tiempo de ciclo, ocupacion activa y unidades top. */
@RestController
@RequestMapping("/api/report")
@PreAuthorize("hasRole('Admin')")
public class ReportController {

    private final ReportService service;
    private final StreamKpiService streamKpiService;

    public ReportController(ReportService service, StreamKpiService streamKpiService) {
        this.service = service;
        this.streamKpiService = streamKpiService;
    }

    @GetMapping("/kpis")
    public KpisResponse kpis(@RequestParam(required = false) String range) {
        return service.kpis(range);
    }

    /** State store de Kafka Streams consultado en caliente (Interactive Queries). */
    @GetMapping("/stream-kpis")
    public List<ReservationKpi> streamKpis() {
        return streamKpiService.kpis();
    }

    @GetMapping("/top-units")
    public List<TopUnitResponse> topUnits(@RequestParam(required = false) String range,
                                           @RequestParam(defaultValue = "10") int limit) {
        return service.topUnits(range, limit);
    }
}
