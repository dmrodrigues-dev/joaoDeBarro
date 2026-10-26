package model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import enums.StatusCobranca;
import enums.TipoCobranca;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Cobranca implements Identificavel{
    @CsvBindByName private Long id;
    @CsvBindByName private Long moradorId;
    @CsvBindByName private Long apartamentoId;
    @CsvBindByName private BigDecimal valor;
    @CsvBindByName @CsvDate("yyyy-MM-dd") LocalDate vencimento;
    @CsvBindByName @CsvDate("yyyy-MM-dd") LocalDate dataPagamento;
    @CsvBindByName private TipoCobranca tipo;
    @CsvBindByName private StatusCobranca status;
    @CsvBindByName private Long reservaId;

    public Cobranca() {}

    public Cobranca(Long reservaId, TipoCobranca tipo, Long moradorId, LocalDate vencimento, BigDecimal valor, Long apartamentoId) {
        this.reservaId = reservaId;
        this.tipo = tipo;
        this.moradorId = moradorId;
        this.vencimento = vencimento;
        this.valor = valor;
        this.status = StatusCobranca.PENDENTE;
        this.apartamentoId = apartamentoId;
    }

    public boolean estaAtrasada(LocalDate hoje) {
        return status == StatusCobranca.PENDENTE && hoje.isAfter(vencimento);
    }

    public long diasAtraso(LocalDate hoje) {
        return estaAtrasada(hoje) ? ChronoUnit.DAYS.between(hoje, vencimento) : 0;
    }

    public BigDecimal valorComEncargos(LocalDate hoje) {
        long dias  = diasAtraso(hoje);
        if (dias == 0) return valor;
        BigDecimal multa = valor.multiply(new BigDecimal("0.02"));
        BigDecimal juros = valor.multiply(new BigDecimal("0.00033"))
                .multiply(BigDecimal.valueOf(dias));
        return valor.add(juros).add(multa).setScale(2, RoundingMode.HALF_UP);
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getApartamentoId() {
        return apartamentoId;
    }

    public void setApartamentoId(Long apartamentoId) {
        this.apartamentoId = apartamentoId;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public TipoCobranca getTipo() {
        return tipo;
    }

    public void setTipo(TipoCobranca tipo) {
        this.tipo = tipo;
    }

    public StatusCobranca getStatus() {
        return status;
    }

    public void setStatus(StatusCobranca status) {
        this.status = status;
    }
}
