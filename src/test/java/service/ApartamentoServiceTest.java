package service;

import model.Apartamento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApartamentoServiceTest extends ServiceTestSupport {
    @Test
    public void cadastraEBuscaApartamento() {
        ApartamentoService service = new ApartamentoService(apartamentos);
        service.cadastrarApartamento("A", "101");
        Apartamento encontrado = service.buscarPorNumeroTorre("A", "101");
        assertNotNull(encontrado);
        assertEquals("101", encontrado.getNumero());
        assertEquals("A", encontrado.getTorre());
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    public void naoDuplicaApartamentoComMesmoNumeroETorre() {
        ApartamentoService service = new ApartamentoService(apartamentos);
        service.cadastrarApartamento("A", "101");
        service.cadastrarApartamento("A", "101");
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    public void permiteMesmoNumeroEmTorreDiferenteERemove() {
        ApartamentoService service = new ApartamentoService(apartamentos);
        service.cadastrarApartamento("A", "101");
        service.cadastrarApartamento("B", "101");
        assertEquals(2, service.listarTodos().size());
        service.removerApartamento("A", "101");
        assertNull(service.buscarPorNumeroTorre("A", "101"));
        assertNotNull(service.buscarPorNumeroTorre("B", "101"));
    }
}
