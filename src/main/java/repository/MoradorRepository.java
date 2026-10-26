package repository;

import model.Morador;

import java.nio.file.Path;

public class MoradorRepository extends CsvRepository<Morador>{

    public MoradorRepository() {
        super(Path.of("data/morador.csv"), Morador.class);
    }

    public Morador buscarCpf(String cpf) {
        return dados.stream()
                .filter(e -> e.getCpf().equals(cpf)).findFirst()
                .orElse(null);
    }
}
