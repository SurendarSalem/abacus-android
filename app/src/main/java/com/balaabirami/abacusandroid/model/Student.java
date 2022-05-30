package com.balaabirami.abacusandroid.model;

import android.os.Parcelable;
import android.text.TextUtils;

import androidx.room.Ignore;

import java.util.List;
import java.util.Objects;

public class Student extends User implements Parcelable, Cloneable {

    private String studentId;
    private String enrollDate;
    private String lastOrderedDate;
    private String approveDate;
    private String address;
    private String fatherName;
    private String motherName;
    private String franchise;
    private Level level;
    private String cost;
    private Program program;
    private boolean isCompletedCourse;
    private List<String> items;
    @Ignore
    private boolean promotedAAtoMA = false;

    public Student(String studentId, String enrollDate, String approveDate, String lastOrderedDate, String address, String fatherName, String motherName, String franchise, Level level, String cost, Program program, List<String> items, boolean isCompletedCourse) {
        this.studentId = studentId;
        this.enrollDate = enrollDate;
        this.approveDate = approveDate;
        this.lastOrderedDate = lastOrderedDate;
        this.address = address;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.franchise = franchise;
        this.level = level;
        this.cost = cost;
        this.program = program;
        this.items = items;
        this.isCompletedCourse = isCompletedCourse;
    }

    public Student() {
    }

    public static String getCostValue(User student) {
        if (student.getState() == null) {
            return "1300";
        } else {
            if (student.getState().equalsIgnoreCase("TN")) {
                return "1100";
            } else if (student.getState().equalsIgnoreCase("UK")) {
                return "2000";
            }
        }
        return "1300";
    }

    public boolean isPromotedAAtoMA() {
        return promotedAAtoMA;
    }

    public void setPromotedAAtoMA(boolean promotedAAtoMA) {
        this.promotedAAtoMA = promotedAAtoMA;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(String approveDate) {
        this.approveDate = approveDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getFranchise() {
        return franchise;
    }

    public void setFranchise(String franchise) {
        this.franchise = franchise;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getLastOrderedDate() {
        return lastOrderedDate;
    }

    public void setLastOrderedDate(String lastOrderedDate) {
        this.lastOrderedDate = lastOrderedDate;
    }

    public boolean isCompletedCourse() {
        return isCompletedCourse;
    }

    public void setCompletedCourse(boolean completedCourse) {
        isCompletedCourse = completedCourse;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", enrollDate='" + enrollDate + '\'' +
                ", lastOrderedDate='" + lastOrderedDate + '\'' +
                ", approveDate='" + approveDate + '\'' +
                ", address='" + address + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", motherName='" + motherName + '\'' +
                ", franchise='" + franchise + '\'' +
                ", level=" + level +
                ", cost='" + cost + '\'' +
                ", program=" + program +
                ", isCompletedCourse=" + isCompletedCourse +
                ", items=" + items +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    public static boolean isValidForEnroll(Student user) {
        error = "";
        if (user == null) {
            error = "Please enter all the details!";
            return false;
        }
        if (TextUtils.isEmpty(user.getStudentId())) {
            error = "Please enter Student ID!";
            return false;
        }
        if (TextUtils.isEmpty(user.getEnrollDate()) || user.getEnrollDate().equalsIgnoreCase("DD/MM/YYYY")) {
            error = "Please enter enroll date!";
            return false;
        }
        if (user.getEnrollDate().contains("D") || user.getEnrollDate().contains("M") || user.getEnrollDate().contains("Y")) {
            error = "Please enter a valid enroll date!";
            return false;
        }
        if (TextUtils.isEmpty(user.getName())) {
            error = "Please enter Name!";
            return false;
        }
        if (TextUtils.isEmpty(user.getState())) {
            error = "Please select state!";
            return false;
        }
        if (TextUtils.isEmpty(user.getCity())) {
            error = "Please select city!";
            return false;
        }
        if (TextUtils.isEmpty(user.getAddress())) {
            error = "Please enter address!";
            return false;
        }
        if (TextUtils.isEmpty(user.getState())) {
            error = "Please enter state!";
            return false;
        }
        if (TextUtils.isEmpty(user.getCity())) {
            error = "Please enter city!";
            return false;
        }
        if (TextUtils.isEmpty(user.getContactNo())) {
            error = "Please enter contact number!";
            return false;
        }
        if (TextUtils.isEmpty(user.getEmail())) {
            error = "Email cannot be empty!";
            return false;
        }
        if (TextUtils.isEmpty(user.getFatherName())) {
            error = "Father name cannot be empty!";
            return false;
        }
        if (TextUtils.isEmpty(user.getMotherName())) {
            error = "Mother name cannot be empty!";
            return false;
        }
        /*if (TextUtils.isEmpty(user.getFranchise())) {
            error = "Franchise ID cannot be empty!";
            return false;
        }*/
        if (user.getLevel() == null) {
            error = "Please select a level!";
            return false;
        }
        if (user.getItems() == null || user.getItems().isEmpty()) {
            error = "Please select atleast one item!";
            return false;
        }
        if (user.getProgram() == null) {
            error = "Please select a program!";
            return false;
        }
        if (TextUtils.isEmpty(user.getCost())) {
            error = "Please select a cost!";
            return false;
        }
        if (TextUtils.isEmpty(user.getApproveDate()) || user.getApproveDate().equalsIgnoreCase("DD/MM/YYYY")) {
            error = "Please enter a valid approve date!";
            return false;
        }
        if (user.getApproveDate().contains("D") || user.getApproveDate().contains("M") || user.getApproveDate().contains("Y")) {
            error = "Please enter a valid approve date!";
            return false;
        }

        return true;
    }

}
