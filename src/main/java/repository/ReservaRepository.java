package repository;

import model.Reserva;
import enums.StatusReserva;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ReservaRepository extends CsvRepository<Reserva> {

    public ReservaRepository() {
        super(Path.of("data/reserva.csv"), Reserva.class);
    }

    public List<Reserva> buscarPorAreaData(
            Long areaId, LocalDate data) {

        return listarTudo().stream()
                .filter(r -> r.getAreaId().equals(areaId)
                        && r.getData().equals(data))
                .toList();
    }

    public List<Reserva> buscarPorMorador(Long moradorId) {
        return listarTudo().stream()
                .filter(r -> r.getMoradorId().equals(moradorId))
                .toList();
    }

    public List<Reserva> buscarAtivas() {
        return listarTudo().stream()
                .filter(r -> r.getStatus() == StatusReserva.RESERVADO)
                .toList();
    }

    public List<Reserva> buscarPorData(LocalDate data) {
        return listarTudo().stream()
                .filter(r -> r.getData().equals(data))
                .toList();
    }
}