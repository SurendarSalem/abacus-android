package com.balaabirami.abacusandroid.utils;

import com.google.firebase.database.GenericTypeIndicator;

import java.util.List;

public interface DataLoadListener<T> {
    void onDataLoaded(List<T> data);
}
