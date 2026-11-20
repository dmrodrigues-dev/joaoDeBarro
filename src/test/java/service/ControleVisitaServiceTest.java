package service;

import enums.StatusVisita;
import model.RegistroVisita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.RegistroVisitaRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControleVisitaServiceTest extends ServiceTestSupport {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 20);

    // Relógio que avançamos na mão.
    static class RelogioManual extends Clock {
        private Instant agora = Instant.parse("2026-11-20T14:30:00Z");

        void avancar(Duration d) {
            agora = agora.plus(d);
        }

        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return agora; }
    }

    private RelogioManual relogio;
    private ControleVisitaService service;
    private AutorizacaoService autorizacaoService;

    @BeforeEach
    public void prepararCenario() {
        relogio = new RelogioManual();
        service = new ControleVisitaService(moradores, visitantes, autorizacoes, registrosVisita, relogio);
        autorizacaoService = new AutorizacaoService(moradores, visitantes, autorizacoes);

        new ApartamentoService(apartamentos).cadastrarApartamento("12", "C");
        Long apId = apartamentos.buscarPorTorreNumero("12", "C").getId();
        MoradorService moradorService = new MoradorService(apartamentos, moradores);
        moradorService.cadastrarMorador("Ana", "111", apId, 0, 1);
        moradorService.cadastrarMorador("Bruno", "222", apId, 0, 1);
    }

    @Test
    public void entradaComAutorizacaoDeHojeFicaEmAndamento() {
        autorizacaoService.cadastrarAutorizacao("111", HOJE, "333", "Carlos");

        RegistroVisita registro = service.registrarEntrada("333");

        assertNotNull(registro.getId());
        assertEquals(StatusVisita.EM_ANDAMENTO, registro.getStatus());
        assertNull(registro.getSaida());
        assertEquals(1, service.listarEmAndamento().size());
    }

    @Test
    public void negaEntradaSemAutorizacaoParaHoje() {
        autorizacaoService.cadastrarAutorizacao("111", HOJE.plusDays(1), "333", "Carlos");

        assertThrows(IllegalStateException.class, () -> service.registrarEntrada("333"));
        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    public void negaEntradaQuandoOutroMoradorNaoAutorizou() {
        autorizacaoService.cadastrarAutorizacao("111", HOJE, "333", "Carlos");

        assertThrows(IllegalStateException.class, () -> service.registrarEntrada("333", "222"));
    }

    @Test
    public void negaVisitanteOuMoradorInexistente() {
        assertThrows(IllegalArgumentException.class, () -> service.registrarEntrada("999"));
        autorizacaoService.cadastrarAutorizacao("111", HOJE, "333", "Carlos");
        assertThrows(IllegalArgumentException.class, () -> service.registrarEntrada("333", "000"));
        assertThrows(IllegalArgumentException.class, () -> service.registrarEntrada(" "));
    }

    @Test
    public void negaEntradaDuplicadaSemSaida() {
        autorizacaoService.cadastrarAutorizacao("111", HOJE, "333", "Carlos");
        service.registrarEntrada("333");

        assertThrows(IllegalStateException.class, () -> service.registrarEntrada("333"));
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    public void saidaConcluiVisitaECalculaPermanencia() {
        autorizacaoService.cadastrarAutorizacao("111", HOJE, "333", "Carlos");
        service.registrarEntrada("333");

        relogio.avancar(Duration.ofMinutes(95));
        RegistroVisita registro = service.registrarSaida("333");

        assertEquals(StatusVisita.CONCLUIDA, registro.getStatus());
        assertEquals(Duration.ofMinutes(95), registro.permanencia(registro.getSaida()));
        assertTrue(service.listarEmAndamento().isEmpty());
    }

    @Test
    public void podeEntrarDeNovoDepoisDeSair() {
        autorizacaoService.cadastrarAutorizacao("111", HOJE, "333", "Carlos");
        service.registrarEntrada("333");
        service.registrarSaida("333");

        service.registrarEntrada("333");

        assertEquals(2, service.listarTodos().size());
        assertEquals(1, service.listarEmAndamento().size());
    }

    @Test
    public void saidaSemEntradaEhRecusada() {
        new VisitanteService(visitantes).cadastrarVisitante("Carlos", "333");

        assertThrows(IllegalStateException.class, () -> service.registrarSaida("333"));
    }

    @Test
    public void maisDeUmAutorizadorExigeInformarOMorador() {
        autorizacaoService.cadastrarAutorizacao("111", HOJE, "333", "Carlos");
        autorizacaoService.cadastrarAutorizacao("222", HOJE, "333", "Carlos");

        assertEquals(2, service.moradoresAutorizadosHoje("333").size());
        assertThrows(IllegalArgumentException.class, () -> service.registrarEntrada("333"));

        RegistroVisita registro = service.registrarEntrada("333", "222");
        assertEquals(moradores.buscarCpf("222").getId(), registro.getMoradorId());
    }

    @Test
    public void visitaEmAndamentoSobreviveAoRecarregarOCsv() {
        autorizacaoService.cadastrarAutorizacao("111", HOJE, "333", "Carlos");
        service.registrarEntrada("333");

        // Novo repositório lendo o mesmo arquivo (como ao reabrir o programa).
        RegistroVisitaRepository recarregado = new RegistroVisitaRepository();

        assertEquals(1, recarregado.listarTudo().size());
        RegistroVisita lido = recarregado.listarTudo().get(0);
        assertEquals(StatusVisita.EM_ANDAMENTO, lido.getStatus());
        assertNull(lido.getSaida());
        assertEquals(relogio.instant().atZone(ZoneOffset.UTC).toLocalDateTime(), lido.getEntrada());
    }

    @Test
    public void historicoPorPeriodoVisitanteMoradorEResumo() {
        autorizacaoService.cadastrarAutorizacao("111", HOJE, "333", "Carlos");
        autorizacaoService.cadastrarAutorizacao("222", HOJE, "444", "Diana");

        service.registrarEntrada("333");
        relogio.avancar(Duration.ofMinutes(60));
        service.registrarSaida("333");
        service.registrarEntrada("444");

        List<RegistroVisita> dia = service.historicoPorPeriodo(HOJE, HOJE);
        assertEquals(2, dia.size());
        assertTrue(service.historicoPorPeriodo(HOJE.plusDays(1), HOJE.plusDays(2)).isEmpty());
        assertEquals(1, service.historicoPorVisitante("333").size());
        assertEquals(1, service.historicoPorMorador("222").size());

        ControleVisitaService.Resumo resumo = service.resumo(HOJE, HOJE);
        assertEquals(2, resumo.total());
        assertEquals(1, resumo.concluidas());
        assertEquals(1, resumo.emAndamento());
        assertEquals(60, resumo.tempoMedioMinutos());

        assertThrows(IllegalArgumentException.class, () -> service.historicoPorPeriodo(HOJE, HOJE.minusDays(1)));
    }
}
