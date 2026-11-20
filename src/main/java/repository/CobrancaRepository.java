package repository;

import enums.TipoCobranca;
import model.Cobranca;

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

    public List<Cobranca> buscarPendentesMorador(
            Long moradorId, LocalDate dia) {

        return listarTudo().stream()
                .filter(c -> c.getMoradorId().equals(moradorId)
                        && c.estaAtrasada(dia))
                .toList();
    }

    public Cobranca buscarTaxaReserva(Long reservaId) {
        return listarTudo().stream()
                .filter(c -> reservaId.equals(c.getReservaId())
                        && c.getTipo() == TipoCobranca.TAXA_RESERVA)
                .findFirst()
                .orElse(null);
    }

    public List<Cobranca> buscarPorReserva(Long reservaId) {
        return listarTudo().stream()
                .filter(c -> reservaId.equals(c.getReservaId()))
                .toList();
    }
}