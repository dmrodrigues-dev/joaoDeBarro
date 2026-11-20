package service;

import java.math.BigDecimal;
import java.time.LocalDate;

import enums.TipoCobranca;
import enums.StatusCobranca;
import model.Cobranca;
import model.Morador;
import repository.CobrancaRepository;

public class CobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final ManutencaoService manutencaoService;

    public CobrancaService(
            CobrancaRepository cobrancaRepository,
            ManutencaoService manutencaoService) {

        this.cobrancaRepository = cobrancaRepository;
        this.manutencaoService = manutencaoService;
    }

    public void gerarTaxaCondominial(
            Morador morador,
            LocalDate vencimento) {

        BigDecimal valorFixo = new BigDecimal("500.00");
        BigDecimal valorPorVaga = new BigDecimal("50.00");

        BigDecimal adicional = valorPorVaga.multiply(
                BigDecimal.valueOf(morador.getVagasGaragem()));

        BigDecimal valorTotal = valorFixo.add(adicional);

        Cobranca cobranca = new Cobranca(
                null,
                TipoCobranca.TAXA_CONDOMINIAL,
                morador.getId(),
                vencimento,
                valorTotal,
                morador.getApartamentoId());

        cobrancaRepository.salvar(cobranca);
    }

    public void registrarPagamento(Cobranca cobranca) {

        if (cobranca == null || cobranca.getId() == null) {
            throw new IllegalArgumentException("Cobrança inválida.");
        }

        if (cobranca.getStatus() != StatusCobranca.PENDENTE) {
            throw new IllegalArgumentException("Cobrança não está pendente.");
        }

        cobranca.setStatus(StatusCobranca.PAGO);
        cobranca.setDataPagamento(LocalDate.now());

        cobrancaRepository.atualizar(cobranca);
    }

    public BigDecimal calcularReceita() {

        return cobrancaRepository.listarTudo().stream()
                .filter(c -> c.getStatus() == StatusCobranca.PAGO)
                .map(Cobranca::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularInadimplencia() {

        LocalDate hoje = LocalDate.now();

        return cobrancaRepository.listarTudo().stream()
                .filter(c -> c.estaAtrasada(hoje))
                .map(c -> c.valorComEncargos(hoje))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularDespesas() {
        return manutencaoService.calcularTotalGastos();
    }
}
