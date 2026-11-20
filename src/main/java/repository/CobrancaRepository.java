package repository;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import model.Cobranca;

public class CobrancaRepository extends CsvRepository<Cobranca> {

    // Construtor utilizado pelo sistema.
    public CobrancaRepository() {
        super(Path.of("data", "cobranca.csv"), Cobranca.class);
    }

    // Construtor utilizado nos testes.
    public CobrancaRepository(Path caminho) {
        super(caminho, Cobranca.class);
    }

    // Busca cobranças atrasadas de um morador.
    public List<Cobranca> buscarPendentesMorador(
            Long moradorId, LocalDate dia) {

        return dados.stream()
                .filter(c -> Objects.equals(c.getMoradorId(), moradorId)
                        && c.estaAtrasada(dia))
                .toList();
    }

    // Atualiza uma cobrança já cadastrada.
    public void atualizar(Cobranca cobranca) {

        if (cobranca == null || cobranca.getId() == null) {
            throw new IllegalArgumentException("Cobranca invalida.");
        }

        if (buscarPorId(cobranca.getId()) == null) {
            throw new IllegalArgumentException("Cobranca nao encontrada.");
        }

        remover(cobranca.getId());
        salvar(cobranca);
    }
}