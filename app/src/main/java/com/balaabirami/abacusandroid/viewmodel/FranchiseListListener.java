package com.balaabirami.abacusandroid.viewmodel;

import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;

import java.util.List;

public interface FranchiseListListener {
    void onFranchiseListLoaded(User franchise);

    void onFranchiseListLoaded(List<User> franchises);

    void onUserDetailLoaded(User franchises);
}
