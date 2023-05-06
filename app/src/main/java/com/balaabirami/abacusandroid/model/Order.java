package com.balaabirami.abacusandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.TypeConverters;

import com.balaabirami.abacusandroid.BuildConfig;
import com.balaabirami.abacusandroid.room.LevelConvertors;
import com.balaabirami.abacusandroid.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        currentLevel = in.readParcelable(Level.class.getClassLoader());
        orderLevel = in.readParcelable(Level.class.getClassLoader());
        books = in.createStringArrayList();
        certificate = in.readString();
        orderId = in.readString();
        state = in.readString();
        franchiseName = in.readString();
        studentName = in.readString();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(studentId);
        parcel.writeParcelable(currentLevel, i);
        parcel.writeParcelable(orderLevel, i);
        parcel.writeStringList(books);
        parcel.writeString(certificate);
        parcel.writeString(orderId);
        parcel.writeString(state);
        parcel.writeString(franchiseName);
        parcel.writeString(studentName);
        parcel.writeString(date);
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

    public static boolean isValid(Order order, Student student) {
        error = "";
        if (order == null) {
            error = "Please enter all the details!";
            return false;
        }
        if (TextUtils.isEmpty(order.getStudentId())) {
            error = "Please enter Student ID!";
            Log.d("InvalidOrder", error + ":" + order.getOrderId() + "-->" + order.getStudentId());
            return false;
        }
        if (order.getOrderLevel() == null) {
            error = "Please select order level!";
            Log.d("InvalidOrder", error + ":" + order.getOrderId() + "-->" + order.getOrderLevel());
            return false;
        }
        if (order.getBooks() == null || order.getBooks().isEmpty()) {
            error = "Please select a book!";
            Log.d("InvalidOrder", error + ":" + order.getOrderId() + "-->" + order.getBooks());
            return false;
        }
        if (TextUtils.isEmpty(order.getCertificate())) {
            error = "Please select a certificate!";
            Log.d("InvalidOrder", error + ":" + order.getOrderId() + "-->" + order.getCertificate());
            return false;
        }
        /*int diffInDays = UIUtils.diffBetweenDates(UIUtils.getDate(), student.getLastOrderedDate());
        if (diffInDays == -1) {
            error = "Error in date format!";
            return false;
        }
        if (diffInDays >= 0 && diffInDays < 45) {
            error = "You cannot place an order before 45 days from the previous order";
            return false;
        }
        if (diffInDays > 120) {
            error = "You cannot place an order after 120 days from the previous order";
            return false;
        }*/
        return true;
    }

    public static boolean isCorrupted(Order order) {
        return order == null || order.getOrderLevel() == null || order.getCurrentLevel() == null || TextUtils.isEmpty(order.getStudentName());
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
                ", currentLevel=" + currentLevel.toString() +
                ", orderLevel=" + orderLevel.toString() +
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

    public static String getOrderValue(User student) {
        if (student.getState() == null) {
            return "500";
        } else {
            if (student.getState().equalsIgnoreCase("TN")) {
                return "360";
            } else if (student.getState().equalsIgnoreCase("UK")) {
                return "1000";
            }
        }
        return "500";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return getOrderId().equals(order.getOrderId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderId());
    }
}
