package model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import enums.StatusVisita;

import java.time.Duration;
import java.time.LocalDateTime;

/*
 * Registro de uma visita: quem entrou, qual morador recebeu,
 * quando entrou e quando saiu.
 *
 * Guarda apenas os IDs do visitante e do morador (mesmo padrão
 * de Reserva e Autorizacao). Enquanto o visitante não sai,
 * "saida" fica vazia e o status é EM_ANDAMENTO.
 */
public class RegistroVisita implements Identificavel {
    @CsvBindByName private Long id;
    @CsvBindByName private Long visitanteId;
    @CsvBindByName private Long moradorId;
    @CsvBindByName @CsvDate("yyyy-MM-dd HH:mm") private LocalDateTime entrada;
    @CsvBindByName @CsvDate("yyyy-MM-dd HH:mm") private LocalDateTime saida;
    @CsvBindByName private StatusVisita status;

    public RegistroVisita() {}

    public RegistroVisita(Long visitanteId, Long moradorId, LocalDateTime entrada) {
        this.visitanteId = visitanteId;
        this.moradorId = moradorId;
        this.entrada = entrada;
        this.status = StatusVisita.EM_ANDAMENTO;
    }

    // Regra do próprio registro: só pode sair quem ainda está dentro.
    public void registrarSaida(LocalDateTime momento) {
        if (status != StatusVisita.EM_ANDAMENTO) {
            throw new IllegalStateException("A saída desta visita já foi registrada.");
        }
        if (momento.isBefore(entrada)) {
            throw new IllegalArgumentException("A saída não pode ser anterior à entrada.");
        }
        this.saida = momento;
        this.status = StatusVisita.CONCLUIDA;
    }

    public boolean estaEmAndamento() {
        return status == StatusVisita.EM_ANDAMENTO;
    }

    // Tempo de permanência: até a saída, ou até "agora" se ainda estiver no condomínio.
    public Duration permanencia(LocalDateTime agora) {
        return Duration.between(entrada, saida != null ? saida : agora);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getVisitanteId() {
        return visitanteId;
    }

    public void setVisitanteId(Long visitanteId) {
        this.visitanteId = visitanteId;
    }

    public Long getMoradorId() {
        return moradorId;
    }

    public void setMoradorId(Long moradorId) {
        this.moradorId = moradorId;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalDateTime entrada) {
        this.entrada = entrada;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public void setSaida(LocalDateTime saida) {
        this.saida = saida;
    }

    public StatusVisita getStatus() {
        return status;
    }

    public void setStatus(StatusVisita status) {
        this.status = status;
    }
}
