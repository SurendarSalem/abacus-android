package com.balaabirami.abacusandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Order implements Parcelable, Cloneable {
    public static String error;
    private String studentId;
    private Level currentLevel;
    private Level orderLevel;
    private List<String> books;
    private String certificate;
    private String orderId;
    private String state;
    private String franchiseName;
    private String studentName;
    private String date;


    public Order() {
    }

    public Order(String studentId, Level currentLevel, Level orderLevel, List<String> books, String certificate, String orderId, String state, String franchiseName, String studentName, String date) {
        this.studentId = studentId;
        this.currentLevel = currentLevel;
        this.orderLevel = orderLevel;
        this.books = books;
        this.certificate = certificate;
        this.orderId = orderId;
        this.state = state;
        this.franchiseName = franchiseName;
        this.studentName = studentName;
        this.date = date;
    }

    protected Order(Parcel in) {
        studentId = in.readString();
        books = in.createStringArrayList();
        certificate = in.readString();
        orderId = in.readString();
        state = in.readString();
        franchiseName = in.readString();
        studentName = in.readString();
        date = in.readString();
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

    public static String getError() {
        return error;
    }

    public static void setError(String error) {
        Order.error = error;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFranchiseName() {
        return franchiseName;
    }

    public void setFranchiseName(String franchiseName) {
        this.franchiseName = franchiseName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
        parcel.writeString(state);
        parcel.writeString(franchiseName);
        parcel.writeString(studentName);
        parcel.writeString(date);
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public String toString() {
        return "Order{" +
                "studentId='" + studentId + '\'' +
                ", currentLevel=" + currentLevel +
                ", orderLevel=" + orderLevel +
                ", books=" + books +
                ", certificate='" + certificate + '\'' +
                ", orderId='" + orderId + '\'' +
                ", state='" + state + '\'' +
                ", franchiseName='" + franchiseName + '\'' +
                ", studentName='" + studentName + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    @NonNull
    @Override
    protected Order clone() throws CloneNotSupportedException {
        return new Order(this.studentId, this.currentLevel, this.orderLevel.clone(), this.books, this.certificate, this.orderId, this.state, this.franchiseName, this.studentName, this.date);
    }
}
