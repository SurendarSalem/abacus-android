package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.utils.StateHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SignupViewModel extends AndroidViewModel {

    private final MutableLiveData<List<String>> states = new MutableLiveData<>();
    private final MutableLiveData<List<String>> cities = new MutableLiveData<>();
    private final MutableLiveData<List<Level>> levels = new MutableLiveData<>();
    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<User>> result = new MutableLiveData<>();
    boolean isUserAddedInDB, isUserAddedInAuth;

    public SignupViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.USER_REFERENCE);
    }

    public LiveData<List<String>> getStates() {
        if (states.getValue() == null || states.getValue().isEmpty()) {
            states.setValue(StateHelper.getInstance().getStateNames(getApplication().getApplicationContext()));
        }
        return states;
    }

    public LiveData<List<Level>> getLevels() {
        if (levels.getValue() == null || levels.getValue().isEmpty()) {
            levels.setValue(LevelRepository.newInstance().getLevels());
        }
        return levels;
    }

    public LiveData<List<String>> getCities(int pos) {
        cities.setValue(StateHelper.getInstance().getStates(getApplication()).get(pos).getDistricts());
        cities.getValue().add(0, "Select a city");
        return cities;
    }

   /* public void signup(User franchise) {
        result.setValue(Resource.loading(null, null));
        firebaseHelper.addUser(franchise, nothing -> {
            firebaseHelper.createUser(franchise, task -> {
                if (task.isSuccessful()) {
                    result.setValue(Resource.success(franchise));
                } else {
                    result.setValue(Resource.error("User not added!", null));
                }
            });
        }, e -> {
            result.setValue(Resource.error(e.getMessage(), null));
        });
    }*/

    public void signup(User franchise) {
        result.setValue(Resource.loading(null, null));
        firebaseHelper.createUser(franchise, signupTask -> {
            if (signupTask.isSuccessful()) {
                firebaseHelper.login(franchise, loginTask -> {
                    if (loginTask.isSuccessful()) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            franchise.setFirebaseId(firebaseUser.getUid());
                            firebaseHelper.addUser(franchise, nothing -> {
                                result.setValue(Resource.success(franchise));
                            }, e -> {
                                result.setValue(Resource.error(e.getMessage(), null));
                            });
                        } else {
                            result.setValue(Resource.error("Login failed!", null));
                        }
                    } else {
                        result.setValue(Resource.error("Login failed!", null));
                    }
                });
            } else {
                result.setValue(Resource.error("User not added!", null));
            }
        });
    }

    public MutableLiveData<Resource<User>> getResult() {
        return result;
    }
}
