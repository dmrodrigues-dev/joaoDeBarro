package model;

import com.opencsv.bean.CsvBindByName;

public abstract class Pessoa implements Identificavel{
    @CsvBindByName protected Long id;
    @CsvBindByName protected String nome;
    @CsvBindByName protected String cpf;

    public Pessoa() {}

    public Pessoa(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
