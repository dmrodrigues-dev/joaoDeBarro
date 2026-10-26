package model;

import java.math.BigDecimal;

public class Churrasqueira extends AreaComum{

    public Churrasqueira(Long id) {
        this.id = id;
        this.nome = "Churrasqueira";
    }

    @Override
    public BigDecimal getTaxa() {
        return BigDecimal.valueOf(80);
    }

    @Override
    public int getMaxHoras() {
        return 4;
    }
}
