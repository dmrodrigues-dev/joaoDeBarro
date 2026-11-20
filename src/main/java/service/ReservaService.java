
package service;

import enums.StatusCobranca;
import enums.StatusReserva;
import enums.TipoCobranca;
import model.AreaComum;
import model.Cobranca;
import model.Morador;
import model.Reserva;
import repository.CobrancaRepository;
import repository.ReservaRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final CobrancaRepository cobrancaRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            CobrancaRepository cobrancaRepository) {

        this.reservaRepository = reservaRepository;
        this.cobrancaRepository = cobrancaRepository;
    }


    public void reservarAreaComum(
            Morador morador,
            AreaComum area,
            int horas,
            LocalTime inicio,
            LocalDate dia) {

        if (morador == null || area == null
                || inicio == null || dia == null) {
            throw new IllegalArgumentException(
                    "Preencha todos os dados da reserva."
            );
        }

        if (horas <= 0 || horas > area.getMaxHoras()) {
            throw new IllegalArgumentException(
                    "A duração deve ser entre 1 e "
                            + area.getMaxHoras() + " horas."
            );
        }


        LocalDateTime inicioReserva = LocalDateTime.of(dia, inicio);
        LocalDateTime fimReserva = inicioReserva.plusHours(horas);

        if (inicioReserva.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Não é possível reservar uma data ou horário que já passou."
            );
        }

        if (!fimReserva.toLocalDate().equals(dia)) {
            throw new IllegalArgumentException(
                    "A reserva não pode ultrapassar a meia-noite."
            );
        }

        LocalTime fim = fimReserva.toLocalTime();

        boolean conflito = reservaRepository
                .buscarPorAreaData(area.getId(), dia)
                .stream()
                .filter(r -> r.getStatus() == StatusReserva.RESERVADO)
                .anyMatch(r -> {
                    LocalTime fimExistente =
                            r.getInicio().plusHours(r.getHoras());

                    return inicio.isBefore(fimExistente)
                            && fim.isAfter(r.getInicio());
                });

        if (conflito) {
            throw new IllegalStateException(
                    "Este horário já está reservado."
            );
        }


        boolean inadimplente = cobrancaRepository
                .buscarPendentesMorador(morador.getId(), dia)
                .stream()
                .anyMatch(c -> c.diasAtraso(dia) > 30);

        if (inadimplente) {
            throw new IllegalStateException(
                    "Morador bloqueado: existem taxas atrasadas há mais de 30 dias."
            );
        }


        Reserva reserva = new Reserva(
                area.getId(),
                morador.getId(),
                dia,
                inicio,
                horas
        );

        reservaRepository.salvar(reserva);


        if (area.getTaxa().compareTo(BigDecimal.ZERO) > 0) {

            Cobranca taxa = new Cobranca(
                    reserva.getId(),
                    TipoCobranca.TAXA_RESERVA,
                    morador.getId(),
                    dia.plusDays(30),
                    area.getTaxa(),
                    morador.getApartamentoId()
            );

            cobrancaRepository.salvar(taxa);
        }
    }


    public List<Reserva> listarReservas() {
        return reservaRepository.listarTudo();
    }


    public List<Reserva> listarReservasAtivas() {
        return reservaRepository.buscarAtivas();
    }


    public Reserva buscarPorId(Long id) {
        Reserva reserva = reservaRepository.buscarPorId(id);

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "Reserva não encontrada."
            );
        }

        return reserva;
    }


    public List<Reserva> buscarPorMorador(Long moradorId) {
        return reservaRepository.buscarPorMorador(moradorId);
    }


    public List<Reserva> buscarPorData(LocalDate data) {
        return reservaRepository.buscarPorData(data);
    }


    public void cancelarReserva(Long reservaId) {

        Reserva reserva = buscarPorId(reservaId);

        if (reserva.getStatus() != StatusReserva.RESERVADO) {
            throw new IllegalStateException(
                    "Esta reserva não está ativa."
            );
        }

        LocalDateTime agora = LocalDateTime.now();

        LocalDateTime inicioReserva = LocalDateTime.of(
                reserva.getData(),
                reserva.getInicio()
        );

        if (!agora.isBefore(inicioReserva)) {
            throw new IllegalStateException(
                    "Não é possível cancelar uma reserva que já começou."
            );
        }


        Duration tempoRestante = Duration.between(
                agora, inicioReserva
        );


        reserva.setStatus(StatusReserva.CANCELADO);
        reservaRepository.atualizar(reserva);

        Cobranca cobrancaOriginal =
                cobrancaRepository.buscarTaxaReserva(reservaId);

        if (cobrancaOriginal == null) {
            return;
        }


        cobrancaOriginal.setStatus(StatusCobranca.CANCELADO);
        cobrancaRepository.atualizar(cobrancaOriginal);


        if (tempoRestante.compareTo(Duration.ofHours(48)) >= 0) {
            return;
        }


        BigDecimal multa = cobrancaOriginal.getValor()
                .multiply(new BigDecimal("0.50"));

        if (multa.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }


        Cobranca cobrancaCancelamento = new Cobranca(
                reserva.getId(),
                TipoCobranca.CANCELAMENTO_RESERVA,
                cobrancaOriginal.getMoradorId(),
                LocalDate.now().plusDays(30),
                multa,
                cobrancaOriginal.getApartamentoId()
        );

        cobrancaRepository.salvar(cobrancaCancelamento);
    }


    public void concluirReserva(Long reservaId) {

        Reserva reserva = buscarPorId(reservaId);

        if (reserva.getStatus() != StatusReserva.RESERVADO) {
            throw new IllegalStateException(
                    "Somente reservas ativas podem ser concluídas."
            );
        }

        reserva.setStatus(StatusReserva.CONCLUIDO);
        reservaRepository.atualizar(reserva);
    }
}