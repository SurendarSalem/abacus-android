package com.balaabirami.abacusandroid.repository;

import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Student;

import java.util.List;

public class OrdersRepository {

    static OrdersRepository repository;
    private List<Order> orders;

    public static OrdersRepository getInstance() {
        if (repository == null) {
            repository = new OrdersRepository();
        }
        return repository;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Order> getOrders() {
        return orders;
    }


    public void clear() {
        setOrders(null);
        repository = null;
    }
}
