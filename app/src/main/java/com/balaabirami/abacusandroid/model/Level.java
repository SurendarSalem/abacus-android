package com.balaabirami.abacusandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Level implements Cloneable, Parcelable {

    private String name;
    private Type type;
    private boolean selected;
    private int level;

    public Level() {
    }

    public Level(String name, Type type, int level) {
        this.name = name;
        this.type = type;
        this.level = level;
    }

    protected Level(Parcel in) {
        name = in.readString();
        selected = in.readByte() != 0;
        level = in.readInt();
    }

    public static final Creator<Level> CREATOR = new Creator<Level>() {
        @Override
        public Level createFromParcel(Parcel in) {
            return new Level(in);
        }

        @Override
        public Level[] newArray(int size) {
            return new Level[size];
        }
    };

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

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeByte((byte) (selected ? 1 : 0));
        parcel.writeInt(level);
    }

    public enum Type {
        LEVEL1,
        LEVEL2,
        LEVEL3,
        LEVEL4,
        LEVEL5,
        LEVEL6;
    }


    public String toString() {
        return "Level{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", selected=" + selected +
                ", level=" + level +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Level levelObj = (Level) o;
        return name.equals(levelObj.name) && type == levelObj.type && level == levelObj.level;
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
