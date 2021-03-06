package com.balaabirami.abacusandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User implements Parcelable, Cloneable {

    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_FRANCHISE = 2;
    public static final int TYPE_STUDENT = 3;
    public static final int TYPE_HEADER = 4;
    public static final int TYPE_MASTER_FRANCHISE = 5;
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

    public User() {
    }

    public User(String name, String email, String password, String confirmPassword, int accountType, String id, String firebaseId, String contactNo, String username, String registerDate, boolean isApproved, boolean isIsAdmin, boolean selected, String state, String city) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.accountType = accountType;
        this.id = id;
        this.firebaseId = firebaseId;
        this.contactNo = contactNo;
        this.username = username;
        this.registerDate = registerDate;
        this.isApproved = isApproved;
        this.isIsAdmin = isIsAdmin;
        this.selected = selected;
        this.state = state;
        this.city = city;
    }

    protected User(Parcel in) {
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(confirmPassword);
        dest.writeInt(accountType);
        dest.writeString(id);
        dest.writeString(firebaseId);
        dest.writeString(contactNo);
        dest.writeString(username);
        dest.writeString(registerDate);
        dest.writeByte((byte) (isApproved ? 1 : 0));
        dest.writeByte((byte) (isIsAdmin ? 1 : 0));
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeString(state);
        dest.writeString(city);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static boolean isValid(User user) {
        error = "";
        if (user == null) {
            error = "Please enter valid credentials!";
            return false;
        }
        if (TextUtils.isEmpty(user.getEmail())) {
            error = "Email cannot be empty!";
            return false;
        }
        if (TextUtils.isEmpty(user.getPassword())) {
            error = "Password cannot be empty!";
            return false;
        }
        if (!isValidPassword(user.getPassword())) {
            error = "Password should contain atleast one uppercase and one special character.";
            return false;
        }
        return true;
    }

    public static boolean isValidForSignup(User user) {
        error = "";
        if (user == null) {
            error = "Please enter all the details!";
            return false;
        }
        if (TextUtils.isEmpty(user.getId())) {
            error = "Please enter franchise ID!";
            return false;
        }
        if (TextUtils.isEmpty(user.getName())) {
            error = "Name cannot be empty!";
            return false;
        }
        if (TextUtils.isEmpty(user.getEmail())) {
            error = "Email cannot be empty!";
            return false;
        }
        if (TextUtils.isEmpty(user.getContactNo())) {
            error = "Contact Number cannot be empty!";
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
        if (TextUtils.isEmpty(user.getUsername())) {
            error = "Username cannot be empty!";
            return false;
        }
        if (TextUtils.isEmpty(user.getPassword())) {
            error = "Password cannot be empty!";
            return false;
        }
        if (TextUtils.isEmpty(user.getRegisterDate()) || user.getRegisterDate().equals("DD/MM/YYYY")) {
            error = "Date cannot be empty!";
            return false;
        }
        if (user.getRegisterDate().contains("D") || user.getRegisterDate().contains("M") || user.getRegisterDate().contains("Y")) {
            error = "Please enter a valid date!";
            return false;
        }
        if (TextUtils.isEmpty(user.getPassword())) {
            error = "Password cannot be empty!";
            return false;
        }
        if (!isValidPassword(user.getPassword())) {
            error = "Password should contain atleast one uppercase and one special character.";
            return false;
        }
        if (TextUtils.isEmpty(user.getConfirmPassword())) {
            error = "Confirm password cannot be empty!";
            return false;
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            error = "Passwords doesn't match! ";
            return false;
        }
        return true;
    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
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

    public static String createFranchiseID() {
        return "FR" + System.currentTimeMillis();
    }

    public static String createStudentID() {
        return "ST" + System.currentTimeMillis();
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public boolean isIsAdmin() {
        return isIsAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        isIsAdmin = isAdmin;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }


    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
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
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


    public String createID() {
        return email.substring(0, email.indexOf("@"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email) && id.equals(user.id);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    @NonNull
    @Override
    protected User clone() throws CloneNotSupportedException {
        return new User(this.name, this.email, this.password, this.confirmPassword, this.accountType, this.id, this.firebaseId, this.contactNo, this.username, this.registerDate, this.isApproved, this.isIsAdmin, this.selected, this.state, this.city);
    }
}
