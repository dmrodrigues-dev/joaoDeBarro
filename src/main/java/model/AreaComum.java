package model;

import com.opencsv.bean.CsvBindByName;

import java.math.BigDecimal;

public abstract class AreaComum implements Identificavel{
    @CsvBindByName protected Long id;
    @CsvBindByName protected String nome;

    public abstract BigDecimal getTaxa();
    public abstract int getMaxHoras();

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
}
