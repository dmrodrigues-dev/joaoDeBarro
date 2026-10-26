package model;

import java.math.BigDecimal;

public class Piscina extends AreaComum{

    public Piscina(Long id) {
        this.id = id;
        this.nome = "Piscina";
    }

    @Override
    public BigDecimal getTaxa() {
        return BigDecimal.valueOf(0);
    }

    @Override
    public int getMaxHoras() {
        return 2;
    }
}
