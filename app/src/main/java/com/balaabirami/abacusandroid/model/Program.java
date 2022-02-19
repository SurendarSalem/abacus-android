package com.balaabirami.abacusandroid.model;

import java.util.Objects;

public class Program {
    private Course course;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
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
