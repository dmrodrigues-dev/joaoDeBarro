package service;

import model.Visitante;
import repository.VisitanteRepository;

import java.util.List;

public class VisitanteService {
    private final VisitanteRepository visitanteRepository;

    public VisitanteService(VisitanteRepository visitanteRepository) {
        this.visitanteRepository = visitanteRepository;
    }

    public List<Visitante> listarTodos() {
        List<Visitante> lista = visitanteRepository.listarTudo();
        if (lista.isEmpty()) System.out.println("Não há visitantes cadastrados.");
        return lista;
    }

    public Visitante buscarPorCpf(String cpf) {
        Visitante visitante = visitanteRepository.buscarCpf(cpf);
        if (visitante == null) System.out.println("Visitante não encontrado.");
        return visitante;
    }

    public void cadastrarVisitante(String nome, String cpf) {
        if (visitanteRepository.buscarCpf(cpf) != null) {
            System.out.println("Já existe uma visitante com esse CPF.");
            return;
        }

        Visitante visitante = new Visitante(nome,  cpf);
        visitanteRepository.salvar(visitante);
        System.out.println("Visitante cadastrado com sucesso.");
    }

    public void removerVisitante(String cpf) {
        Visitante visitante = visitanteRepository.buscarCpf(cpf);
        if (visitante == null) {
            System.out.println("Visitante não encontrado.");
            return;
        }

        visitanteRepository.remover(visitante.getId());
        System.out.println("Visitante removido com sucesso.");
    }
}
