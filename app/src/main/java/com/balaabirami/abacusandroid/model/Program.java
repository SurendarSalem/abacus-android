package com.balaabirami.abacusandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Program implements Parcelable {
    private Course course;

    protected Program(Parcel in) {
        int tmpCourse = in.readInt();
        this.course = tmpCourse == -1 ? null : Course.values()[tmpCourse];
    }

    public static final Creator<Program> CREATOR = new Creator<Program>() {
        @Override
        public Program createFromParcel(Parcel in) {
            return new Program(in);
        }

        @Override
        public Program[] newArray(int size) {
            return new Program[size];
        }
    };

    public Program() {

    }

    public static Program getMA() {
        Program ma = new Program();
        ma.setCourse(Program.Course.MA);
        return ma;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.course == null ? -1 : this.course.ordinal());
    }

    public enum Course {
        MA,
        AA
    }

    @Override
    public String toString() {
        return "Program{" +
                "course=" + course +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return course == program.course;
    }

    @Override
    public int hashCode() {
        return Objects.hash(course);
    }
}
