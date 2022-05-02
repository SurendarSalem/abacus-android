package com.balaabirami.abacusandroid.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class OrderList {
    private String header;
    private Order order;

    public OrderList(String header, Order order) {
        this.header = header;
        this.order = order;
    }

    public static List<OrderList> createOrderListFromOrder(List<Order> data) {
        HashMap<String, List<Order>> orderMap = new HashMap<>();
        List<OrderList> orderLists = new ArrayList<>();
        for (Order order : data) {
            if (!orderMap.containsKey(order.getFranchiseName())) {
                List<Order> orderArray = new ArrayList<>();
                orderArray.add(order);
                orderMap.put(order.getFranchiseName(), orderArray);
            } else {
                orderMap.get(order.getFranchiseName()).add(order);
            }
        }
        for (String header : orderMap.keySet()) {
            OrderList orderList = new OrderList(header, null);
            orderLists.add(orderList);
            for (Order order : orderMap.get(header)) {
                orderLists.add(new OrderList(null, order));
            }
        }
        return orderLists;
    }

    public static List<OrderList> createOrderListWithHeaders(List<OrderList> data) {
        HashMap<String, List<Order>> orderMap = new HashMap<>();
        List<OrderList> orderLists = new ArrayList<>();
        for (OrderList order : data) {
            if (!orderMap.containsKey(order.getOrder().getFranchiseName())) {
                List<Order> orderArray = new ArrayList<>();
                orderArray.add(order.getOrder());
                orderMap.put(order.getOrder().getFranchiseName(), orderArray);
            } else {
                orderMap.get(order.getOrder().getFranchiseName()).add(order.getOrder());
            }
        }
        for (String header : orderMap.keySet()) {
            OrderList orderList = new OrderList(header, null);
            orderLists.add(orderList);
            for (Order order : Objects.requireNonNull(orderMap.get(header))) {
                orderLists.add(new OrderList(null, order));
            }
        }
        return orderLists;
    }

    public static List<Order> createOrdersFromOrderList(List<OrderList> orderLists) {
        List<Order> orders = new ArrayList<>();
        for (OrderList orderList : orderLists) {
            if (orderList.getOrder() != null) {
                orders.add(orderList.getOrder());
            }
        }
        return orders;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
