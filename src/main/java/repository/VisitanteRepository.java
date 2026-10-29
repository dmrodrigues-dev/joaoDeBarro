package repository;

import model.Visitante;

import java.nio.file.Path;

public class VisitanteRepository extends CsvRepository<Visitante>{

    public VisitanteRepository() {
        super(Path.of("data/visitante.csv"),  Visitante.class);
    }

    public Visitante buscarCpf(String cpf) {
        return dados.stream()
                .filter(v -> v.getCpf().equals(cpf))
                .findFirst().orElse(null);
    }
}
