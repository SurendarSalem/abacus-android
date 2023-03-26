package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Session;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.room.AbacusDatabase;
import com.balaabirami.abacusandroid.room.OrderDao;
import com.balaabirami.abacusandroid.room.OrderLog;
import com.balaabirami.abacusandroid.utils.StateHelper;
import com.balaabirami.abacusandroid.utils.UIUtils;

import java.util.List;
import java.util.Objects;

public class EnrollViewModel extends AndroidViewModel {

    private final MutableLiveData<List<String>> states = new MutableLiveData<>();
    private final MutableLiveData<List<String>> cities = new MutableLiveData<>();
    private final MutableLiveData<List<Level>> levels = new MutableLiveData<>();
    private final MutableLiveData<Resource<User>> result = new MutableLiveData<>();
    private final FirebaseHelper firebaseHelper;
    OrderDao orderDao;

    public EnrollViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.STUDENTS_REFERENCE);
        orderDao = Objects.requireNonNull(AbacusDatabase.Companion.getAbacusDatabase(application.getApplicationContext())).orderDao();
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

    public void enroll(Student student, List<Stock> stocks, User currentUser) {
        new Thread(() -> {
            orderDao.insert(new OrderLog(student.getStudentId(), "Enroll API called"));
        }).start();
        Session.Companion.addStep("Enroll API called");
        result.setValue(Resource.loading(null, null));
        firebaseHelper.enrollStudent(student, nothing -> {
            new Thread(() -> {
                orderDao.insert(new OrderLog(student.getStudentId(), "Enroll API success"));
            }).start();
            Session.Companion.addStep("Enroll API success");
            result.setValue(Resource.success(student));
            if (!UIUtils.IS_DATA_IMPORT) {
                firebaseHelper.updateLastStudentId(Integer.parseInt(student.getStudentId()));
            }
            new Thread(() -> {
                orderDao.insert(new OrderLog(student.getStudentId(), "Update Stock API calling"));
            }).start();
            Session.Companion.addStep("Update Stock API calling");
            updateStockUsedInEnroll(student, stocks, currentUser);
        }, e -> {
            new Thread(() -> {
                orderDao.insert(new OrderLog(student.getStudentId(), "Enroll API failed"));
            }).start();
            Session.Companion.addStep("Enroll API failed");
            result.setValue(Resource.error(e.getMessage(), null));
        });
    }

    private void updateStockUsedInEnroll(Student student, List<Stock> stocks, User
            currentUser) {
        new Thread(() -> {
            orderDao.insert(new OrderLog(student.getStudentId(), "Update Stock API called"));
        }).start();
        Session.Companion.addStep("Update Stock API called");
        firebaseHelper.updateStock(student, stocks, currentUser);
    }


    public MutableLiveData<Resource<User>> getResult() {
        return result;
    }

    public void updateStock(Student student, List<Stock> stocks, User currentUser) {
        firebaseHelper.updateStock(student, stocks, currentUser);
    }
}
