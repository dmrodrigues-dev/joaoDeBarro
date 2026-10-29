package service;

import model.Autorizacao;
import model.Morador;
import model.Visitante;
import repository.AutorizacaoRepository;
import repository.MoradorRepository;
import repository.VisitanteRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AutorizacaoService {

    private final MoradorRepository moradorRepository;
    private final VisitanteRepository visitanteRepository;
    private final AutorizacaoRepository autorizacaoRepository;

    public AutorizacaoService(MoradorRepository moradorRepository, VisitanteRepository visitanteRepository, AutorizacaoRepository autorizacaoRepository) {
        this.moradorRepository = moradorRepository;
        this.visitanteRepository = visitanteRepository;
        this.autorizacaoRepository = autorizacaoRepository;
    }

    public List<Autorizacao> listarTodos() {
        List<Autorizacao> lista = autorizacaoRepository.listarTudo();
        if (lista.isEmpty()) System.out.println("Não há autorizações cadastradas.");
        return lista;
    }

    public List<Autorizacao> buscarPorVisitanteDia(String cpfVisitante, LocalDate dia) {
        Visitante visitante = visitanteRepository.buscarCpf(cpfVisitante);
        if (visitante == null) {
            System.out.println("Visitante não encontrado.");
            return List.of();
        }

        List<Autorizacao> lista = autorizacaoRepository.buscarVisitanteData(visitante.getId(), dia);
        if (lista.isEmpty()) System.out.println("Não há autorização para o visitante no dia fornecido.");
        return lista;
    }

    public Autorizacao buscarPorMoradorVisitanteDia(String cpfMorador, String cpfVisitante, LocalDate dia) {
        Morador morador = moradorRepository.buscarCpf(cpfMorador);
        if (morador == null) {
            System.out.println("Morador não encontrado.");
            return new Autorizacao();
        }

        Visitante visitante = visitanteRepository.buscarCpf(cpfVisitante);
        if (visitante == null) {
            System.out.println("Visitante não encontrado.");
            return new Autorizacao();
        }

        Autorizacao autorizacao = autorizacaoRepository.buscarMoradorVisitanteData(morador.getId(), visitante.getId(), dia);
        if (autorizacao == null) {
            System.out.println("Autorização não encontrada.");
            return new Autorizacao();
        }
        return autorizacao;
    }

    public void cadastrarAutorizacao(String cpfMorador, LocalDate dia, String cpfVisitante, String nomeVisitante) {
        Morador morador = moradorRepository.buscarCpf(cpfMorador);
        if (morador == null) {
            System.out.println("Morador não encontrado.");
            return;
        }

        Visitante visitante = visitanteRepository.buscarCpf(cpfVisitante);
        if (visitante == null) {
            System.out.println("Cadastrar novo visitante.");
            visitante =  new Visitante(nomeVisitante, cpfVisitante);
            visitanteRepository.salvar(visitante);
        }

        Autorizacao autorizacao = autorizacaoRepository.buscarMoradorVisitanteData(morador.getId(), visitante.getId(), dia);
        if (autorizacao != null) {
            System.out.println("O visitante já está autorizado no dia fornecido.");
            return;
        }
        autorizacao = new Autorizacao(morador.getId(), visitante.getId(), dia);
        autorizacaoRepository.salvar(autorizacao);
        System.out.println("Autorização cadastrada com sucesso.");
    }

    public void removerAutorizacao(String cpfMorador, LocalDate dia, String cpfVisitante) {
        Autorizacao autorizacao = buscarPorMoradorVisitanteDia(cpfMorador, cpfVisitante, dia);
        autorizacaoRepository.remover(autorizacao.getId());
        System.out.println("Autorização removida com sucesso.");
    }
}
