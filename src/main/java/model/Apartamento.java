package model;

import com.opencsv.bean.CsvBindByName;

public class Apartamento implements Identificavel{
    @CsvBindByName private Long id;
    @CsvBindByName private String torre;
    @CsvBindByName private String numero;

    public Apartamento() {}

    public Apartamento(String torre, String numero) {
        this.torre = torre;
        this.numero = numero;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTorre() {
        return torre;
    }

    public void setTorre(String torre) {
        this.torre = torre;
    }
}
