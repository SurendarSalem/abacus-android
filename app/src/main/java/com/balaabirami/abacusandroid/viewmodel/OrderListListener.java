package com.balaabirami.abacusandroid.viewmodel;

import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Student;

import java.util.List;

public interface OrderListListener {

    void onOrderListLoaded(List<Order> orders);

    void onError(String message);
}
