package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import repository.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class ServiceTestSupport {
    private final Path dataDir = Path.of("data");
    private final String[] arquivosCsv = {
            "apartamento.csv",
            "morador.csv",
            "visitante.csv",
            "autorizacao.csv",
            "reserva.csv",
            "cobranca.csv"
    };
    private final Map<Path, byte[]> backups = new HashMap<>();

    ApartamentoRepository apartamentos;
    MoradorRepository moradores;
    VisitanteRepository visitantes;
    AutorizacaoRepository autorizacoes;
    ReservaRepository reservas;
    CobrancaRepository cobrancas;

    @BeforeEach
    public void prepararRepositoriosIsolados() throws IOException {
        Files.createDirectories(dataDir);
        backups.clear();

        for (String nome : arquivosCsv) {
            Path arquivo = dataDir.resolve(nome);
            if (Files.exists(arquivo)) {
                backups.put(arquivo, Files.readAllBytes(arquivo));
                Files.delete(arquivo);
            }
        }

        apartamentos = new ApartamentoRepository();
        moradores = new MoradorRepository();
        visitantes = new VisitanteRepository();
        autorizacoes = new AutorizacaoRepository();
        reservas = new ReservaRepository();
        cobrancas = new CobrancaRepository();
    }

    @AfterEach
    public void restaurarArquivosOriginais() throws IOException {
        for (String nome : arquivosCsv) {
            Files.deleteIfExists(dataDir.resolve(nome));
        }

        for (Map.Entry<Path, byte[]> backup : backups.entrySet()) {
            Files.write(backup.getKey(), backup.getValue());
        }
    }
}
