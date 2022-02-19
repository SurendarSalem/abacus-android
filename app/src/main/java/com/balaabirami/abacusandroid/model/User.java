package com.balaabirami.abacusandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User implements Parcelable {

    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_FRANCHISE = 2;
    public static final int TYPE_STUDENT = 3;
    public static String error;

    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private int accountType;
    private String id;
    private String contactNo;
    private String username;
    private String registerDate;

    public User(Parcel in) {
        name = in.readString();
        email = in.readString();
        password = in.readString();
        confirmPassword = in.readString();
        accountType = in.readInt();
        id = in.readString();
        contactNo = in.readString();
        username = in.readString();
        registerDate = in.readString();
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

    public User() {
    }

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

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", accountType='" + accountType + '\'' +
                ", id='" + id + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", username='" + username + '\'' +
                ", registerDate='" + registerDate + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(confirmPassword);
        parcel.writeInt(accountType);
        parcel.writeString(id);
        parcel.writeString(contactNo);
        parcel.writeString(username);
        parcel.writeString(registerDate);
    }

    public String createID() {
        return email.substring(0, email.indexOf("@"));
    }
}
