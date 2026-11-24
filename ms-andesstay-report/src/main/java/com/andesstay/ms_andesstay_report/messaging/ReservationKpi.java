package com.andesstay.ms_andesstay_report.messaging;

/**
 * Resultado de la agregacion en ventanas: cuantos eventos de reserva se emitieron para una
 * unidad dentro de una ventana de 1 minuto. Se publica en "reservations.kpis" y tambien se
 * puede consultar en el state store mediante el endpoint /api/report/stream-kpis.
 */
public class ReservationKpi {

    private String unitId;
    private long windowStart;
    private long windowEnd;
    private long reservations;

    public ReservationKpi() {
    }

    public ReservationKpi(String unitId, long windowStart, long windowEnd, long reservations) {
        this.unitId = unitId;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.reservations = reservations;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public long getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(long windowStart) {
        this.windowStart = windowStart;
    }

    public long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(long windowEnd) {
        this.windowEnd = windowEnd;
    }

    public long getReservations() {
        return reservations;
    }

    public void setReservations(long reservations) {
        this.reservations = reservations;
    }
}
