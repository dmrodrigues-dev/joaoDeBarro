package model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.time.LocalDate;

public class Autorizacao implements Identificavel{
    @CsvBindByName private Long id;
    @CsvBindByName private Long moradorId;
    @CsvBindByName private Long visitanteId;
    @CsvBindByName @CsvDate("yyyy-MM-dd") private LocalDate dia;

    public Autorizacao() {}

    public Autorizacao(Long moradorId, Long visitanteId, LocalDate dia) {
        this.moradorId = moradorId;
        this.visitanteId = visitanteId;
        this.dia = dia;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getMoradorId() {
        return moradorId;
    }

    public void setMoradorId(Long moradorId) {
        this.moradorId = moradorId;
    }

    public Long getVisitanteId() {
        return visitanteId;
    }

    public void setVisitanteId(Long visitanteId) {
        this.visitanteId = visitanteId;
    }

    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }
}
