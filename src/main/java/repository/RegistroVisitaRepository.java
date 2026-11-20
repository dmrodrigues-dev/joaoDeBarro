package repository;

import enums.StatusVisita;
import model.RegistroVisita;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class RegistroVisitaRepository extends CsvRepository<RegistroVisita> {

    public RegistroVisitaRepository() {
        super(Path.of("data/registro_visita.csv"), RegistroVisita.class);
    }

    // Visita em andamento de um visitante (null se ele não está no condomínio).
    public RegistroVisita buscarAtivoPorVisitante(Long visitanteId) {
        return dados.stream()
                .filter(r -> r.getStatus() == StatusVisita.EM_ANDAMENTO
                        && r.getVisitanteId().equals(visitanteId))
                .findFirst().orElse(null);
    }

    public List<RegistroVisita> buscarAtivos() {
        return dados.stream()
                .filter(r -> r.getStatus() == StatusVisita.EM_ANDAMENTO)
                .toList();
    }

    public List<RegistroVisita> buscarPorVisitante(Long visitanteId) {
        return dados.stream()
                .filter(r -> r.getVisitanteId().equals(visitanteId))
                .toList();
    }

    public List<RegistroVisita> buscarPorMorador(Long moradorId) {
        return dados.stream()
                .filter(r -> r.getMoradorId().equals(moradorId))
                .toList();
    }

    // Visitas cuja ENTRADA caiu entre as duas datas (inclusive).
    public List<RegistroVisita> buscarPorPeriodo(LocalDate de, LocalDate ate) {
        return dados.stream()
                .filter(r -> !r.getEntrada().toLocalDate().isBefore(de)
                        && !r.getEntrada().toLocalDate().isAfter(ate))
                .toList();
    }
}
