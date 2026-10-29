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
        List<Morador> lista = moradorRepository.listarTudo();
        if (lista.isEmpty()) System.out.println("Não há moradores cadastrados.");
        return lista;
    }

    public Morador buscarPorCpf(String cpf) {
        Morador morador = moradorRepository.buscarCpf(cpf);
        if (morador == null) System.out.println("Morador não encontrado.");
        return morador;
    }

    public void cadastrarMorador(String nome, String cpf, Long idApartamento, int qtdPets, int vagasGaragem) {
        if (moradorRepository.buscarCpf(cpf) != null) {
            System.out.println("Já existe uma morador com esse CPF.");
            return;
        }

        if (apartamentoRepository.buscarPorId(idApartamento) == null) {
            System.out.println("Não foi encontrado um apartamento com o ID fornecido.");
            return;
        }

        Morador morador = new Morador(nome,  cpf, idApartamento, qtdPets, vagasGaragem);
        moradorRepository.salvar(morador);
        System.out.println("Morador cadastrado com sucesso.");
    }

    public void removerMorador(String cpf) {
        Morador morador = moradorRepository.buscarCpf(cpf);
        if (morador == null) {
            System.out.println("Morador não encontrado.");
            return;
        }

        moradorRepository.remover(morador.getId());
        System.out.println("Morador removido com sucesso.");
    }
}
