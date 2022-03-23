package com.balaabirami.abacusandroid.viewmodel;

import com.balaabirami.abacusandroid.model.User;

public interface LastStudentIdListener {
    void onLastStudentIdLoaded(int lastStudentId);
    void onError(String error);
}
