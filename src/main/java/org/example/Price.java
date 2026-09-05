package org.example;

public class Price {
    public String articul;
    public Integer price;
    public Price(String articul, Integer price){
        this.articul = articul;
        this.price = price;
    }

    public String getArticul() {
        return articul;
    }

    public Integer getPrice() {
        return price;
    }
}
