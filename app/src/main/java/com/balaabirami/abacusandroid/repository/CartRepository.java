package com.balaabirami.abacusandroid.repository;

import com.balaabirami.abacusandroid.model.Cart;
import com.balaabirami.abacusandroid.model.CartOrder;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    static CartRepository repository;
    private Cart cart;

    public static CartRepository getInstance() {
        if (repository == null) {
            repository = new CartRepository();
        }
        return repository;
    }

    public static CartRepository getRepository() {
        return repository;
    }

    public static void setRepository(CartRepository repository) {
        CartRepository.repository = repository;
    }

    public Cart getCart() {
        if (cart == null) {
            cart = new Cart();
            setCart(cart);
        }
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public boolean addToCart(CartOrder cartOrder) {
        if (getCart().getOrders() == null) {
            getCart().setOrders(new ArrayList<>());
        }
        if (getCart().getOrders().contains(cartOrder)) {
            getCart().getOrders().remove(cartOrder);
            return false;
        } else {
            getCart().getOrders().add(cartOrder);
            return true;
        }
    }

    public void clearAll() {
        if (getCart().getOrders() != null) {
            getCart().getOrders().clear();
        }
    }
}
