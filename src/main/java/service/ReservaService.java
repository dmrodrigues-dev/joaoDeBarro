package service;

import enums.StatusReserva;
import enums.TipoCobranca;
import model.AreaComum;
import model.Cobranca;
import model.Morador;
import model.Reserva;
import repository.CobrancaRepository;
import repository.ReservaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final CobrancaRepository cobrancaRepository;

    public ReservaService(ReservaRepository reservaRepository, CobrancaRepository cobrancaRepository) {
        this.reservaRepository = reservaRepository;
        this.cobrancaRepository = cobrancaRepository;
    }

    public void reservarAreaComum(Morador morador, AreaComum area, int horas, LocalTime inicio, LocalDate dia) {
        if (horas > area.getMaxHoras() || horas <= 0) {
            System.out.println("Quantidade de horas inválido.");
            return;
        }

        LocalTime fim = inicio.plusHours(horas);
        boolean conflito = reservaRepository.buscarPorAreaData(area.getId(), dia).stream()
                .filter(r -> r.getStatus() == StatusReserva.RESERVADO)
                        .anyMatch(r -> inicio.isBefore(r.getInicio().plusHours(r.getHoras())) && fim.isAfter(r.getInicio()));

        if (conflito) {
            System.out.println("Horário da nova reserva indisponivel.");
            return;
        }

        boolean atrasado = cobrancaRepository.buscarPendentesMorador(morador.getId(), dia).stream()
                .anyMatch(c -> c.diasAtraso(dia) >= 30);

        if (atrasado) {
            System.out.println("Morador tem taxas em atraso.");
            return;
        }

        Reserva reserva = new Reserva(area.getId(), morador.getId(), dia, inicio, horas);
        reservaRepository.salvar(reserva);
        Cobranca taxa = new Cobranca(reserva.getId(), TipoCobranca.TAXA_RESERVA, morador.getId(), dia.plusDays(30), area.getTaxa(), morador.getApartamentoId());
        cobrancaRepository.salvar(taxa);
        System.out.println("Reserva realizada.");

    }
}
