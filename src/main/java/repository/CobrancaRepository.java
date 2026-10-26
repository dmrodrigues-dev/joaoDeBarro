package repository;

import model.Cobranca;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class CobrancaRepository extends CsvRepository<Cobranca>{

    public CobrancaRepository() {
        super(Path.of("data/cobranca.csv"), Cobranca.class);
    }

    public List<Cobranca> buscarPendentesMorador(Long moradorId,  LocalDate dia) {
        return dados.stream()
                .filter(c -> c.getMoradorId() == moradorId && c.estaAtrasada(dia))
                .toList();
    }
}
