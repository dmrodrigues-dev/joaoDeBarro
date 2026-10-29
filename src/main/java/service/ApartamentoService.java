package service;

import model.Apartamento;
import repository.ApartamentoRepository;

import java.util.List;

public class ApartamentoService {
    private final ApartamentoRepository apartamentoRepository;

    public ApartamentoService(ApartamentoRepository apartamentoRepository) {
        this.apartamentoRepository = apartamentoRepository;
    }

    public List<Apartamento> listarTodos(){
        List<Apartamento> lista = apartamentoRepository.listarTudo();
        if (lista.isEmpty()) System.out.println("Não há apartamentos cadastrados.");
        return lista;
    }

    public Apartamento buscarPorNumeroTorre(String torre, String numero) {
        Apartamento ap = apartamentoRepository.buscarPorTorreNumero(torre, numero);
        if (ap == null) System.out.println("Apartamento não encontrado.");
        return ap;
    }

    public void cadastrarApartamento(String torre, String numero) {
        if (apartamentoRepository.buscarPorTorreNumero(torre,  numero) != null) {
            System.out.println("Já existe um apartamento com este número e torre.");
            return;
        }

        Apartamento apartamento = new Apartamento(torre, numero);
        apartamentoRepository.salvar(apartamento);
        System.out.println("Apartamento cadastrado com sucesso.");
    }

    public void removerApartamento(String torre, String numero) {
        Apartamento apartamento = apartamentoRepository.buscarPorTorreNumero(torre,  numero);
        if (apartamento == null) {
            System.out.println("Apartamento não encontrado.");
            return;
        }

        apartamentoRepository.remover(apartamento.getId());
        System.out.println("Apartamento removido com sucesso.");
    }
}
