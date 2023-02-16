package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.FranchiseRepository;

import java.util.List;

public class FranchiseListViewModel extends AndroidViewModel implements FranchiseListListener {

    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<List<User>>> franchiseListData = new MutableLiveData<>();
    private final MutableLiveData<Resource<User>> franchiseUpdateData = new MutableLiveData<>();
    FranchiseRepository franchiseRepository;

    public FranchiseListViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.USER_REFERENCE);
        franchiseRepository = FranchiseRepository.getInstance();
    }

    public void getAllFranchises() {
        franchiseListData.setValue(Resource.loading(null, null));
        firebaseHelper.getAllFranchises(this);
    }

    public void approveFranchise(User user) {
        franchiseUpdateData.setValue(Resource.loading(null, null));
        firebaseHelper.approveFranchise(user, nothing -> {
            franchiseUpdateData.setValue(Resource.success(user));
        }, e -> {
            franchiseUpdateData.setValue(Resource.error(e.getMessage(), null));
        });
    }

    public MutableLiveData<Resource<List<User>>> getFranchiseListData() {
        List<User> franchises = FranchiseRepository.getInstance().getFranchises();
        if (franchises == null || franchises.isEmpty()) {
            getAllFranchises();
        } else {
            franchiseListData.setValue(Resource.loading(null, null));
            franchiseListData.setValue(Resource.success(franchises));
        }
        return franchiseListData;
    }

    public MutableLiveData<Resource<User>> getFranchiseUpdateData() {
        return franchiseUpdateData;
    }

    @Override
    public void onFranchiseListLoaded(User franchise) {

    }

    @Override
    public void onFranchiseListLoaded(List<User> franchises) {
        for (User franchise : franchises) {
            franchiseRepository.setFranchises(franchises);
            franchiseListData.setValue(Resource.success(franchises));
        }
    }

    @Override
    public void onUserDetailLoaded(User franchises) {

    }
}