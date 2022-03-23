package com.balaabirami.abacusandroid.model;

import androidx.annotation.NonNull;

import java.util.List;

public class State {
    private String name;
    private List<String> districts;
    private boolean selected;

    public State() {
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
}
