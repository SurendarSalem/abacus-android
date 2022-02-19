package com.balaabirami.abacusandroid.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Level implements Cloneable {

    private String name;
    private Type type;

    public Level() {
    }

    public Level(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        ADMISSION,
        LEVEL1,
        LEVEL2,
        LEVEL3,
        LEVEL4,
        LEVEL5,
        LEVEL6;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Level level = (Level) o;
        return name.equals(level.name) && type == level.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @NonNull
    @Override
    public Level clone() throws CloneNotSupportedException {
        Level clone = new Level();
        clone.setName(this.name);
        clone.setType(this.type);
        return clone;
    }
}
