package model;

import com.opencsv.bean.CsvBindByName;

public class Morador extends Pessoa{
    @CsvBindByName private Long apartamentoId;
    @CsvBindByName private int qtdPets;
    @CsvBindByName private int vagasGaragem;

    public Morador() {}

    public Morador(String nome, String cpf, Long apartamentoId, int qtdPets, int vagasGaragem) {
        super(nome, cpf);
        this.apartamentoId = apartamentoId;
        this.qtdPets = qtdPets;
        this.vagasGaragem = vagasGaragem;
    }

    public Long getApartamentoId() {
        return apartamentoId;
    }

    public void setApartamentoId(Long apartamentoId) {
        this.apartamentoId = apartamentoId;
    }

    public int getQtdPets() {
        return qtdPets;
    }

    public void setQtdPets(int qtdPets) {
        this.qtdPets = qtdPets;
    }

    public int getVagasGaragem() {
        return vagasGaragem;
    }

    public void setVagasGaragem(int vagasGaragem) {
        this.vagasGaragem = vagasGaragem;
    }
}
