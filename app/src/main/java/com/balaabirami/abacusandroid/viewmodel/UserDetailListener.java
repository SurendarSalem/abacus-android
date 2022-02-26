package com.balaabirami.abacusandroid.viewmodel;

import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.User;

import java.util.List;

public interface UserDetailListener {
    void onUserDetailLoaded(User user);
}
