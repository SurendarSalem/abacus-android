package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.utils.StateHelper;

import java.util.List;

public class EnrollViewModel extends AndroidViewModel {

    private final MutableLiveData<List<String>> states = new MutableLiveData<>();
    private final MutableLiveData<List<String>> cities = new MutableLiveData<>();
    private final MutableLiveData<List<Level>> levels = new MutableLiveData<>();
    private final MutableLiveData<Resource<User>> result = new MutableLiveData<>();
    private final FirebaseHelper firebaseHelper;

    public EnrollViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.STUDENTS_REFERENCE);
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

    public void enroll(Student student) {
        result.setValue(Resource.loading(null));
        firebaseHelper.enrollStudent(student, nothing -> {
            result.setValue(Resource.success(student));
        }, e -> {
            result.setValue(Resource.error(e.getMessage(), null));
        });
    }

    public MutableLiveData<Resource<User>> getResult() {
        return result;
    }

    public void updateStock(Student student, List<Stock> stocks, User currentUser) {
        firebaseHelper.updateStock(student, stocks, currentUser);
    }
}
