package service;

import model.Autorizacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


public class AutorizacaoServiceTest extends ServiceTestSupport {
    @Test
    public void autorizaVisitanteEConsultaPorDia() {
        new ApartamentoService(apartamentos).cadastrarApartamento("12", "C");
        Long apId = apartamentos.buscarPorTorreNumero("12", "C").getId();
        new MoradorService(apartamentos, moradores).cadastrarMorador("Ana", "111", apId, 0, 1);
        AutorizacaoService service = new AutorizacaoService(moradores, visitantes, autorizacoes);
        LocalDate dia = LocalDate.of(2026, 11, 1);
        service.cadastrarAutorizacao("111", dia, "333", "Carlos");
        assertEquals(1, service.buscarPorVisitanteDia("333", dia).size());
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    public void naoDuplicaAutorizacaoNoMesmoDiaEPermiteOutroDia() {
        new ApartamentoService(apartamentos).cadastrarApartamento("12", "C");
        Long apId = apartamentos.buscarPorTorreNumero("12", "C").getId();
        new MoradorService(apartamentos, moradores).cadastrarMorador("Ana", "111", apId, 0, 1);
        new VisitanteService(visitantes).cadastrarVisitante("Carlos", "333");
        AutorizacaoService service = new AutorizacaoService(moradores, visitantes, autorizacoes);
        LocalDate dia = LocalDate.of(2026, 11, 1);
        service.cadastrarAutorizacao("111", dia, "333", "Carlos");
        service.cadastrarAutorizacao("111", dia, "333", "Carlos");
        service.cadastrarAutorizacao("111", dia.plusDays(1), "333", "Carlos");
        assertEquals(2, service.listarTodos().size());
    }


    @Test
    public void buscaAutorizacaoPorMoradorVisitanteEDiaERemove() {
        new ApartamentoService(apartamentos).cadastrarApartamento("12", "C");
        Long apId = apartamentos.buscarPorTorreNumero("12", "C").getId();
        new MoradorService(apartamentos, moradores).cadastrarMorador("Ana", "111", apId, 0, 1);
        new VisitanteService(visitantes).cadastrarVisitante("Carlos", "333");
        AutorizacaoService service = new AutorizacaoService(moradores, visitantes, autorizacoes);
        LocalDate dia = LocalDate.of(2026, 11, 1);
        service.cadastrarAutorizacao("111", dia, "333", "Carlos");
        Autorizacao autorizacao = service.buscarPorMoradorVisitanteDia("111", "333", dia);
        assertNotNull(autorizacao.getId());
        service.removerAutorizacao("111", dia, "333");
        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    public void consultaDiaDiferenteNaoRetornaAutorizacao() {
        new ApartamentoService(apartamentos).cadastrarApartamento("C", "12");
        Long apId = apartamentos.buscarPorTorreNumero("C", "12").getId();
        new MoradorService(apartamentos, moradores).cadastrarMorador("Ana", "111", apId, 0, 1);
        AutorizacaoService service = new AutorizacaoService(moradores, visitantes, autorizacoes);
        LocalDate dia = LocalDate.of(2026, 11, 1);
        service.cadastrarAutorizacao("111", dia, "333", "Carlos");
        assertTrue(service.buscarPorVisitanteDia("333", dia.plusDays(1)).isEmpty());
    }
}
