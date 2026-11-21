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
        assertThrows(IllegalArgumentException.class, () -> {
            service.cadastrarApartamento("A", "101");
        });
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    public void permiteMesmoNumeroEmTorreDiferente() {
        ApartamentoService service = new ApartamentoService(apartamentos);
        service.cadastrarApartamento("A", "101");
        service.cadastrarApartamento("B", "101");
        assertEquals(2, service.listarTodos().size());
    }

    @Test
    public void buscaApartamentoInexistenteDeveLancarExcecao() {
        ApartamentoService service = new ApartamentoService(apartamentos);

        assertThrows(IllegalArgumentException.class, () ->
                service.buscarPorNumeroTorre("A", "999")
        );
    }

    @Test
    public void removeApartamentoCadastrado() {
        ApartamentoService service = new ApartamentoService(apartamentos);

        service.cadastrarApartamento("A", "101");

        service.removerApartamento("A", "101");

        assertTrue(service.listarTodos().isEmpty());

        assertThrows(IllegalArgumentException.class, () ->
                service.buscarPorNumeroTorre("A", "101")
        );
    }

    @Test
    public void removerApartamentoInexistenteDeveLancarExcecao() {
        ApartamentoService service = new ApartamentoService(apartamentos);

        assertThrows(IllegalArgumentException.class, () ->
                service.removerApartamento("A", "999")
        );

        assertTrue(service.listarTodos().isEmpty());
    }
}
