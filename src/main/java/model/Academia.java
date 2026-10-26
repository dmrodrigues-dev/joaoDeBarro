package model;

import java.math.BigDecimal;

public class Academia extends AreaComum{

    public Academia(Long id) {
        this.id = id;
        this.nome = "Academia";
    }

    @Override
    public BigDecimal getTaxa() {
        return BigDecimal.ZERO;
    }

    @Override
    public int getMaxHoras() {
        return 2;
    }
}
