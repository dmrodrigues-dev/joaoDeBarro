package repository;

import enums.TipoCobranca;
import model.Cobranca;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class CobrancaRepository extends CsvRepository<Cobranca> {

    public CobrancaRepository() {
        super(Path.of("data/cobranca.csv"), Cobranca.class);
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