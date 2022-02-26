package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.User;

import java.util.List;

public class FranchiseListViewModel extends AndroidViewModel implements FranchiseListListener {

    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<User>> franchiseListData = new MutableLiveData<>();
    private final MutableLiveData<Resource<User>> franchiseUpdateData = new MutableLiveData<>();


    public FranchiseListViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.USER_REFERENCE);
    }

    public void getAllFranchises() {
        franchiseListData.setValue(Resource.loading(null));
        firebaseHelper.getAllFranchises(this);
    }

    public void approveFranchise(User user) {
        franchiseUpdateData.setValue(Resource.loading(null));
        firebaseHelper.approveFranchise(user, nothing -> {
            franchiseUpdateData.setValue(Resource.success(user));
        }, e -> {
            franchiseUpdateData.setValue(Resource.error(e.getMessage(), null));
        });
    }

    public MutableLiveData<Resource<User>> getFranchiseListData() {
        return franchiseListData;
    }

    public MutableLiveData<Resource<User>> getFranchiseUpdateData() {
        return franchiseUpdateData;
    }

    @Override
    public void onFranchiseListLoaded(User franchise) {
        if (franchise.getAccountType() != User.TYPE_ADMIN) {
            franchiseListData.setValue(Resource.success(franchise));
        }
    }

    @Override
    public void onFranchiseListLoaded(List<User> franchises) {

    }

    @Override
    public void onUserDetailLoaded(User franchises) {

    }
}