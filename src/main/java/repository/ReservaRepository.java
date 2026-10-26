package repository;

import model.Reserva;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ReservaRepository extends CsvRepository<Reserva> {

    public ReservaRepository() {
        super(Path.of("data/reserva.csv"), Reserva.class);
    }

    public List<Reserva> buscarPorAreaData(Long areaId, LocalDate data) {
        return dados.stream()
                .filter(e -> e.getAreaId().equals(areaId) && e.getData().equals(data))
                .toList();
    }
}
