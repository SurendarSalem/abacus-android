package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.utils.StateHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {

    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<User>> result = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.USER_REFERENCE);
    }

    public void login(User franchise) {
        result.setValue(Resource.loading(null));
        firebaseHelper.login(franchise, task -> {
            if (task.isSuccessful()) {
                getUserDetail(franchise);
            } else {
                result.setValue(Resource.error("Login failed!", null));
            }
        });
    }


    public void getUserDetail(User user) {
        result.setValue(Resource.loading(null));
        firebaseHelper.getUserDetail(user, new UserDetailListener() {
            @Override
            public void onUserDetailLoaded(User user) {
                result.setValue(Resource.success(user));
            }
        });
    }

    public MutableLiveData<Resource<User>> getResult() {
        return result;
    }

    public void logout() {
        firebaseHelper.logout();
        PreferenceHelper.getInstance(getApplication().getApplicationContext()).updateLogin(false);
        PreferenceHelper.getInstance(getApplication().getApplicationContext()).setUserId(null);
        PreferenceHelper.getInstance(getApplication().getApplicationContext()).setIsAdmin(false);
    }
}