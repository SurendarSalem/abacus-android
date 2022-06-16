package com.balaabirami.abacusandroid.model;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class State {
    private String name;
    private List<String> districts;
    private boolean selected;

    public State() {
    }

    public State(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDistricts() {
        return districts;
    }

    public void setDistricts(List<String> districts) {
        this.districts = districts;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean getSelected() {
        return selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public State(String name, List<String> districts, boolean selected) {
        this.name = name;
        this.districts = districts;
        this.selected = selected;
    }

    @NonNull
    @Override
    protected State clone() throws CloneNotSupportedException {
        return new State(this.name, this.districts, this.selected);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return name.equals(state.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    public static State createState(String name) {
        return new State(name);
    }
}
