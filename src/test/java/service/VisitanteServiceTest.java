package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VisitanteServiceTest extends ServiceTestSupport {
    @Test
    public void cadastraBuscaEListaVisitante() {
        VisitanteService service = new VisitanteService(visitantes);
        service.cadastrarVisitante("Carlos", "333");
        assertNotNull(service.buscarPorCpf("333"));
        assertEquals("Carlos", service.buscarPorCpf("333").getNome());
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    public void rejeitaCpfDuplicadoERemoveVisitante() {
        VisitanteService service = new VisitanteService(visitantes);
        service.cadastrarVisitante("Carlos", "333");
        service.cadastrarVisitante("Outro", "333");
        assertEquals(1, service.listarTodos().size());
        service.removerVisitante("333");
        assertNull(service.buscarPorCpf("333"));
    }
}
