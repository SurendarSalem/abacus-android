package com.balaabirami.abacusandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Stock implements Parcelable {
    private String name;
    private long quantity;
    private boolean selected;

    public Stock() {
    }

    public Stock(String name, long quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    protected Stock(Parcel in) {
        name = in.readString();
        quantity = in.readLong();
        selected = in.readByte() != 0;
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return name.equals(stock.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeLong(quantity);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
