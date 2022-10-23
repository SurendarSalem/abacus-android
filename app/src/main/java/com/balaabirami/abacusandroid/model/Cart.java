package com.balaabirami.abacusandroid.model;

import java.util.List;

public class Cart {
    private List<CartOrder> orders;

    public List<CartOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<CartOrder> orders) {
        this.orders = orders;
    }
}
