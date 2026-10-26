package repository;

import model.Apartamento;

import java.nio.file.Path;

public class ApartamentoRepository extends CsvRepository<Apartamento>{

    public ApartamentoRepository() {
        super(Path.of("data/apartamento.csv"), Apartamento.class);
    }

    public Apartamento buscarPorNumeroTorre(String numero, String torre) {
        return dados.stream()
                .filter(a -> a.getNumero().equals(numero) && a.getTorre().equals(torre))
                .findFirst()
                .orElse(null);
    }
}
