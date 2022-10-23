package com.balaabirami.abacusandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

public class CartOrder implements Parcelable {
    private Order order;
    private Student student;
    private List<Stock> stocks;
    private User currentUser;
    private CartOrderType orderType;


    public CartOrder() {
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public CartOrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(CartOrderType orderType) {
        this.orderType = orderType;
    }

    protected CartOrder(Parcel in) {
        order = in.readParcelable(Order.class.getClassLoader());
        student = in.readParcelable(Student.class.getClassLoader());
        stocks = in.createTypedArrayList(Stock.CREATOR);
        currentUser = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<CartOrder> CREATOR = new Creator<CartOrder>() {
        @Override
        public CartOrder createFromParcel(Parcel in) {
            return new CartOrder(in);
        }

        @Override
        public CartOrder[] newArray(int size) {
            return new CartOrder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(order, i);
        parcel.writeParcelable(student, i);
        parcel.writeTypedList(stocks);
        parcel.writeParcelable(currentUser, i);
    }


    public enum CartOrderType {
        ENROLL("Enroll"),
        ORDER("Order");
        private final String type;
        CartOrderType(String type) {
            this.type = type;
        }
    }

    public CartOrder(Order order, Student student, List<Stock> stocks, User currentUser, CartOrderType orderType) {
        this.order = order;
        this.student = student;
        this.stocks = stocks;
        this.currentUser = currentUser;
        this.orderType = orderType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartOrder)) return false;
        CartOrder cartOrder = (CartOrder) o;
        return cartOrder.order.getOrderId().equals(order.getOrderId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(order.getOrderId());
    }

    @Override
    public String toString() {
        return "CartOrder{" +
                "orderFromCart=" + order +
                ", student=" + student +
                ", stocks=" + stocks +
                ", currentUser=" + currentUser +
                ", orderType=" + orderType +
                '}';
    }
}
