package com.balaabirami.abacusandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Order implements Parcelable {
    public static String error;
    private String studentId;
    private Level currentLevel;
    private Level orderLevel;
    private List<String> books;
    private String certificate;
    private String orderId;

    public Order(Parcel in) {
        studentId = in.readString();
        books = in.createStringArrayList();
        certificate = in.readString();
        orderId = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public Order() {
    }

    public static boolean isValid(Order user) {
        error = "";
        if (user == null) {
            error = "Please enter all the details!";
            return false;
        }
        if (TextUtils.isEmpty(user.getStudentId())) {
            error = "Please enter Student ID!";
            return false;
        }
        if (user.getOrderLevel() == null) {
            error = "Please select order level!";
            return false;
        }
        if (user.getBooks() == null || user.getBooks().isEmpty()) {
            error = "Please select a book!";
            return false;
        }
        if (TextUtils.isEmpty(user.getCertificate())) {
            error = "Please select a certificate!";
            return false;
        }

        return true;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Level getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(Level orderLevel) {
        this.orderLevel = orderLevel;
    }

    public List<String> getBooks() {
        if (books == null) {
            books = new ArrayList<>();
        }
        return books;
    }

    public void setBooks(List<String> books) {
        this.books = books;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public static String createOrderId() {
        return "OR" + System.currentTimeMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(studentId);
        parcel.writeStringList(books);
        parcel.writeString(certificate);
        parcel.writeString(orderId);
    }
}
