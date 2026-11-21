package service;

import model.Apartamento;
import model.Morador;
import repository.ApartamentoRepository;
import repository.MoradorRepository;

import java.util.List;

public class MoradorService {
    private final ApartamentoRepository apartamentoRepository;
    private final MoradorRepository moradorRepository;

    public MoradorService(ApartamentoRepository apartamentoRepository, MoradorRepository moradorRepository) {
        this.apartamentoRepository = apartamentoRepository;
        this.moradorRepository = moradorRepository;
    }

    public List<Morador> listarTodos() {
        return moradorRepository.listarTudo();
    }

    public Morador buscarPorCpf(String cpf) {
        Morador morador = moradorRepository.buscarCpf(cpf);
        if (morador == null) throw new IllegalArgumentException("Morador não encontrado.");
        return morador;
    }

    public void cadastrarMorador(String nome, String cpf, Long idApartamento, int qtdPets, int vagasGaragem) {
        if (moradorRepository.buscarCpf(cpf) != null) {
            throw new IllegalArgumentException("Já existe uma morador com esse CPF.");
        }

        if (apartamentoRepository.buscarPorId(idApartamento) == null) {
            throw new IllegalArgumentException("Não foi encontrado um apartamento com o ID fornecido.");
        }

        Morador morador = new Morador(nome,  cpf, idApartamento, qtdPets, vagasGaragem);
        moradorRepository.salvar(morador);
    }

    public void removerMorador(String cpf) {
        Morador morador = moradorRepository.buscarCpf(cpf);
        if (morador == null) {
            throw new IllegalArgumentException("Morador não encontrado.");
        }

        moradorRepository.remover(morador.getId());
    }
}
