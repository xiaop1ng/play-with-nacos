package org.play.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Goods {

    @Id
    private Long id;

    private Long stockNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStockNumber() {
        return stockNumber;
    }

    public void setStockNumber(Long stockNumber) {
        this.stockNumber = stockNumber;
    }
}
