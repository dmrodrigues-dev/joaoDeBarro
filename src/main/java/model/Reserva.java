package model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import enums.StatusReserva;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva implements Identificavel{
    @CsvBindByName private Long id;
    @CsvBindByName private Long areaId;
    @CsvBindByName private Long moradorId;
    @CsvBindByName @CsvDate("yyyy-MM-dd") private LocalDate data;
    @CsvBindByName @CsvDate("HH:mm") private LocalTime inicio;
    @CsvBindByName private int horas;
    @CsvBindByName private StatusReserva status;

    public Reserva() {}

    public Reserva(Long areaId, Long moradorId, LocalDate data, LocalTime inicio, int horas) {
        this.areaId = areaId;
        this.moradorId = moradorId;
        this.data = data;
        this.inicio = inicio;
        this.horas = horas;
        this.status = StatusReserva.RESERVADO;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getMoradorId() {
        return moradorId;
    }

    public void setMoradorId(Long moradorId) {
        this.moradorId = moradorId;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalTime inicio) {
        this.inicio = inicio;
    }
}
