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
        return apartamentoRepository.listarTudo();
    }

    public Apartamento buscarPorNumeroTorre(String torre, String numero) {
        Apartamento ap = apartamentoRepository.buscarPorTorreNumero(torre, numero);
        if (ap == null) throw new IllegalArgumentException("Apartamento não encontrado.");
        return ap;
    }

    public void cadastrarApartamento(String torre, String numero) {
        if (apartamentoRepository.buscarPorTorreNumero(torre,  numero) != null) {
            throw new IllegalArgumentException("Já existe um apartamento com este número e torre.");
        }

        Apartamento apartamento = new Apartamento(torre, numero);
        apartamentoRepository.salvar(apartamento);
    }

    public void removerApartamento(String torre, String numero) {
        Apartamento apartamento = apartamentoRepository.buscarPorTorreNumero(torre,  numero);
        if (apartamento == null) {
            throw new IllegalArgumentException("Apartamento não encontrado.");
        }

        apartamentoRepository.remover(apartamento.getId());
    }
}
