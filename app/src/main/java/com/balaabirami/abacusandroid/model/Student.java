package com.balaabirami.abacusandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import androidx.room.Ignore;

import java.util.List;
import java.util.Objects;

public class Student implements Parcelable {

    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_FRANCHISE = 2;
    public static final int TYPE_STUDENT = 3;
    public static final int TYPE_HEADER = 4;
    public static final int TYPE_MASTER_FRANCHISE = 5;
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

    public static String error;
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private int accountType;
    private String id;
    private String firebaseId;
    private String contactNo;
    private String username;
    private String registerDate;
    private boolean isApproved;
    private boolean isIsAdmin;
    private boolean selected;
    private String state;
    private String city;

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

    protected Student(Parcel in) {
        studentId = in.readString();
        enrollDate = in.readString();
        lastOrderedDate = in.readString();
        approveDate = in.readString();
        address = in.readString();
        fatherName = in.readString();
        motherName = in.readString();
        franchise = in.readString();
        level = in.readParcelable(Level.class.getClassLoader());
        cost = in.readString();
        program = in.readParcelable(Program.class.getClassLoader());
        isCompletedCourse = in.readByte() != 0;
        items = in.createStringArrayList();
        promotedAAtoMA = in.readByte() != 0;
        name = in.readString();
        email = in.readString();
        password = in.readString();
        confirmPassword = in.readString();
        accountType = in.readInt();
        id = in.readString();
        firebaseId = in.readString();
        contactNo = in.readString();
        username = in.readString();
        registerDate = in.readString();
        isApproved = in.readByte() != 0;
        isIsAdmin = in.readByte() != 0;
        selected = in.readByte() != 0;
        state = in.readString();
        city = in.readString();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

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
                ", level=" + level.toString() +
                ", cost='" + cost + '\'' +
                ", program=" + program.toString() +
                ", isCompletedCourse=" + isCompletedCourse +
                ", items=" + items +
                ", promotedAAtoMA=" + promotedAAtoMA +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", accountType=" + accountType +
                ", id='" + id + '\'' +
                ", firebaseId='" + firebaseId + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", username='" + username + '\'' +
                ", registerDate='" + registerDate + '\'' +
                ", isApproved=" + isApproved +
                ", isIsAdmin=" + isIsAdmin +
                ", selected=" + selected +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
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

    public static boolean isCorrupted(Student student) {
        return student == null || student.getName() == null ||
                student.getLevel() == null || student.getProgram() == null;
    }

    public static boolean isValidForEnroll(Student user) {
        error = "";
        if (user == null) {
            error = "Please enter all the details!";
            return false;
        }
        if (TextUtils.isEmpty(user.getStudentId())) {
            error = "Please enter Student ID!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getStudentId());
            return false;
        }
        if (TextUtils.isEmpty(user.getEnrollDate()) || user.getEnrollDate().equalsIgnoreCase("DD/MM/YYYY")) {
            error = "Please enter enroll date!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getEnrollDate());
            return false;
        }
        if (user.getEnrollDate().contains("D") || user.getEnrollDate().contains("M") || user.getEnrollDate().contains("Y")) {
            error = "Please enter a valid enroll date!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getEnrollDate());
            return false;
        }
        if (TextUtils.isEmpty(user.getName())) {
            error = "Please enter Name!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getName());
            return false;
        }
        if (TextUtils.isEmpty(user.getState())) {
            error = "Please select state!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getState());
            return false;
        }
        if (TextUtils.isEmpty(user.getCity())) {
            error = "Please select city!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getCity());
            return false;
        }
        if (TextUtils.isEmpty(user.getAddress())) {
            error = "Please enter address!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getAddress());
            return false;
        }
        if (TextUtils.isEmpty(user.getState())) {
            error = "Please enter state!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getState());
            return false;
        }
        if (TextUtils.isEmpty(user.getCity())) {
            error = "Please enter city!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getCity());
            return false;
        }
        if (TextUtils.isEmpty(user.getContactNo())) {
            error = "Please enter contact number!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getContactNo());
            return false;
        }
        if (TextUtils.isEmpty(user.getEmail())) {
            error = "Email cannot be empty!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getEmail());
            return false;
        }
        if (TextUtils.isEmpty(user.getFatherName())) {
            error = "Father name cannot be empty!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getFatherName());
            return false;
        }
        if (TextUtils.isEmpty(user.getMotherName())) {
            error = "Mother name cannot be empty!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getMotherName());
            return false;
        }
        /*if (TextUtils.isEmpty(user.getFranchise())) {
            error = "Franchise ID cannot be empty!";
            return false;
        }*/
        if (user.getLevel() == null) {
            error = "Please select a level!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getLevel());
            return false;
        }
        if (user.getItems() == null || user.getItems().isEmpty()) {
            error = "Please select atleast one item!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getItems());
            return false;
        }
        if (user.getProgram() == null) {
            error = "Please select a program!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getProgram());
            return false;
        }
        if (TextUtils.isEmpty(user.getCost())) {
            error = "Please select a cost!";
            Log.d("InvalidStudent", user.getStudentId() + "-->" + error + "-->" + user.getCost());
            return false;
        }
        return true;
    }

    public static String getError() {
        return error;
    }

    public static void setError(String error) {
        Student.error = error;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isIsAdmin() {
        return isIsAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        isIsAdmin = isAdmin;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(studentId);
        parcel.writeString(enrollDate);
        parcel.writeString(lastOrderedDate);
        parcel.writeString(approveDate);
        parcel.writeString(address);
        parcel.writeString(fatherName);
        parcel.writeString(motherName);
        parcel.writeString(franchise);
        parcel.writeParcelable(level, i);
        parcel.writeString(cost);
        parcel.writeParcelable(program, i);
        parcel.writeByte((byte) (isCompletedCourse ? 1 : 0));
        parcel.writeStringList(items);
        parcel.writeByte((byte) (promotedAAtoMA ? 1 : 0));
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(confirmPassword);
        parcel.writeInt(accountType);
        parcel.writeString(id);
        parcel.writeString(firebaseId);
        parcel.writeString(contactNo);
        parcel.writeString(username);
        parcel.writeString(registerDate);
        parcel.writeByte((byte) (isApproved ? 1 : 0));
        parcel.writeByte((byte) (isIsAdmin ? 1 : 0));
        parcel.writeByte((byte) (selected ? 1 : 0));
        parcel.writeString(state);
        parcel.writeString(city);
    }
}
