package service;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import enums.StatusCobranca;
import enums.TipoCobranca;
import model.ChamadoManutencao;
import model.Cobranca;
import model.Morador;
import repository.CobrancaRepository;
import repository.ManutencaoRepository;

class CobrancaServiceTest {

    @TempDir
    Path pastaTemporaria;

    private CobrancaService service;
    private CobrancaRepository cobrancaRepository;
    private ManutencaoRepository manutencaoRepository;

    @BeforeEach
    void preparar() {

        Path arquivoCobrancas
                = pastaTemporaria.resolve("cobrancas-teste.csv");

        Path arquivoManutencoes
                = pastaTemporaria.resolve("manutencoes-teste.csv");

        cobrancaRepository
                = new CobrancaRepository(arquivoCobrancas);

        manutencaoRepository
                = new ManutencaoRepository(arquivoManutencoes);

        ManutencaoService manutencaoService
                = new ManutencaoService(manutencaoRepository);

        service = new CobrancaService(
                cobrancaRepository,
                manutencaoService
        );
    }

    @Test
    void deveGerarTaxaCondominialComValorCorreto() {

        Morador morador = new Morador(
                "João",
                "12345678900",
                10L,
                1,
                2
        );

        morador.setId(1L);

        LocalDate vencimento = LocalDate.of(2026, 12, 10);

        service.gerarTaxaCondominial(morador, vencimento);

        var cobrancas = cobrancaRepository.listarTudo();

        assertEquals(1, cobrancas.size());

        assertEquals(
                new BigDecimal("600.00"),
                cobrancas.get(0).getValor()
        );

        assertEquals(
                TipoCobranca.TAXA_CONDOMINIAL,
                cobrancas.get(0).getTipo()
        );
    }

    @Test
    void deveRegistrarPagamentoDeCobranca() {

        Cobranca cobranca = new Cobranca(
                null,
                TipoCobranca.TAXA_CONDOMINIAL,
                1L,
                LocalDate.of(2026, 12, 10),
                new BigDecimal("500.00"),
                10L
        );

        cobrancaRepository.salvar(cobranca);

        service.registrarPagamento(cobranca);

        Cobranca atualizada
                = cobrancaRepository.buscarPorId(cobranca.getId());

        assertEquals(StatusCobranca.PAGO, atualizada.getStatus());

        assertEquals(LocalDate.now(), atualizada.getDataPagamento());
    }

    @Test
    void naoDeveRegistrarPagamentoDeCobrancaJaPaga() {

        Cobranca cobranca = new Cobranca(
                null,
                TipoCobranca.TAXA_CONDOMINIAL,
                1L,
                LocalDate.of(2026, 12, 10),
                new BigDecimal("500.00"),
                10L
        );

        cobrancaRepository.salvar(cobranca);

        service.registrarPagamento(cobranca);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarPagamento(cobranca)
        );
    }

    @Test
    void deveCalcularReceitaSomenteDasCobrancasPagas() {

        Cobranca paga = new Cobranca(
                null, TipoCobranca.TAXA_CONDOMINIAL,
                1L, LocalDate.of(2026, 12, 10),
                new BigDecimal("500.00"), 10L
        );

        Cobranca pendente = new Cobranca(
                null, TipoCobranca.TAXA_CONDOMINIAL,
                2L, LocalDate.of(2026, 12, 10),
                new BigDecimal("600.00"), 20L
        );

        cobrancaRepository.salvar(paga);
        cobrancaRepository.salvar(pendente);

        service.registrarPagamento(paga);

        BigDecimal receita = service.calcularReceita();

        assertEquals(new BigDecimal("500.00"), receita);
    }

    @Test
    void deveCalcularInadimplenciaComEncargos() {

        Cobranca cobranca = new Cobranca(
                null,
                TipoCobranca.TAXA_CONDOMINIAL,
                1L,
                LocalDate.now().minusDays(10),
                new BigDecimal("100.00"),
                10L
        );

        cobrancaRepository.salvar(cobranca);

        BigDecimal inadimplencia = service.calcularInadimplencia();

        assertEquals(new BigDecimal("102.33"), inadimplencia);
    }

    @Test
    void deveCalcularDespesasDeManutencao() {

        ChamadoManutencao chamado = new ChamadoManutencao(
                null,
                "Elevador",
                "Manutenção preventiva"
        );

        chamado.iniciarAtendimento();

        chamado.concluir(new BigDecimal("800.00"));

        manutencaoRepository.salvar(chamado);

        BigDecimal despesas = service.calcularDespesas();

        assertEquals(new BigDecimal("800.00"), despesas);
    }
}
