package repository;

import model.Autorizacao;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class AutorizacaoRepository extends CsvRepository<Autorizacao> {

    public AutorizacaoRepository() {
        super(Path.of("data/autorizacao.csv"), Autorizacao.class);
    }

    public Autorizacao buscarMoradorVisitanteData(Long idMorador, Long idVisitante, LocalDate dia) {
        return dados.stream()
                .filter(a -> a.getDia().equals(dia) && a.getVisitanteId().equals(idVisitante) && a.getMoradorId().equals(idMorador))
                .findFirst().orElse(null);
    }

    public List<Autorizacao> buscarVisitanteData(Long idVisitante, LocalDate dia) {
        return dados.stream()
                .filter(a -> a.getVisitanteId().equals(idVisitante) && a.getDia().equals(dia))
                .toList();
    }
}
