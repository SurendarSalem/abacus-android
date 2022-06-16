package com.balaabirami.abacusandroid.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class OrderList {
    private String header;
    private Order order;
    private HashMap<String, Integer> itemsCountMap = new HashMap<>();

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
            /*Adding Header*/
            OrderList orderList = new OrderList(header, null);
            HashMap<String, Integer> itemsCountMap = new HashMap<>();
            for (Order order : Objects.requireNonNull(orderMap.get(header))) {
                for (String book : order.getBooks()) {
                    if (itemsCountMap.containsKey(book)) {
                        if (itemsCountMap.get(book) != null) {
                            int booksCount = itemsCountMap.get(book);
                            booksCount++;
                            itemsCountMap.put(book, booksCount);
                        }
                    } else {
                        itemsCountMap.put(book, 1);
                    }
                }
                orderList.setItemsCountMap(itemsCountMap);
            }

            orderLists.add(orderList);

            /*Adding Items*/
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
            if (order.getOrder() != null) {
                if (!orderMap.containsKey(order.getOrder().getFranchiseName())) {
                    List<Order> orderArray = new ArrayList<>();
                    orderArray.add(order.getOrder());
                    orderMap.put(order.getOrder().getFranchiseName(), orderArray);
                } else {
                    orderMap.get(order.getOrder().getFranchiseName()).add(order.getOrder());
                }
            }
        }
       /* for (String header : orderMap.keySet()) {
            OrderList orderList = new OrderList(header, null);
            orderLists.add(orderList);
            for (Order order : Objects.requireNonNull(orderMap.get(header))) {
                orderLists.add(new OrderList(null, order));
            }
        }*/
        for (String header : orderMap.keySet()) {
            /*Adding Header*/
            OrderList orderList = new OrderList(header, null);
            HashMap<String, Integer> itemsCountMap = new HashMap<>();
            for (Order order : Objects.requireNonNull(orderMap.get(header))) {
                for (String book : order.getBooks()) {
                    if (itemsCountMap.containsKey(book)) {
                        if (itemsCountMap.get(book) != null) {
                            int booksCount = itemsCountMap.get(book);
                            booksCount++;
                            itemsCountMap.put(book, booksCount);
                        }
                    } else {
                        itemsCountMap.put(book, 1);
                    }
                }
                orderList.setItemsCountMap(itemsCountMap);
            }

            orderLists.add(orderList);

            /*Adding Items*/
            for (Order order : orderMap.get(header)) {
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

    public HashMap<String, Integer> getItemsCountMap() {
        return itemsCountMap;
    }

    public void setItemsCountMap(HashMap<String, Integer> itemsCountMap) {
        this.itemsCountMap = itemsCountMap;
    }

    @Override
    public String toString() {
        return "OrderList{" +
                "header='" + header + '\'' +
                ", order=" + order +
                ", itemsCountMap=" + itemsCountMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderList)) return false;
        OrderList orderList = (OrderList) o;
        return getOrder().equals(orderList.getOrder());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrder());
    }
}
