package com.balaabirami.abacusandroid.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Book implements Cloneable {

    private String name;
    private boolean selected;

    public Book() {
    }

    public Book(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book level = (Book) o;
        return name.equals(level.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @NonNull
    @Override
    public Book clone() throws CloneNotSupportedException {
        Book clone = new Book();
        clone.setName(this.name);
        clone.setSelected(this.selected);
        return clone;
    }
}
