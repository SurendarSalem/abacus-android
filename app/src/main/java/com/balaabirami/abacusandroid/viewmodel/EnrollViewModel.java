package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Certificate;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Program;
import com.balaabirami.abacusandroid.model.Resource;
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
    private final MutableLiveData<Resource<Student>> result = new MutableLiveData<>();
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

    public void enroll(Student student, List<Stock> stocks, User currentUser, Order order) {
        new Thread(() -> {
            orderDao.insert(new OrderLog(student.getStudentId(), "Enroll API called"));
        }).start();
        result.setValue(Resource.loading(null, null));
        firebaseHelper.enrollStudent(student, nothing -> {
            new Thread(() -> {
                orderDao.insert(new OrderLog(student.getStudentId(), "Enroll API success"));
            }).start();
            placeOrder(student, order, stocks, currentUser);
        }, e -> {
            new Thread(() -> {
                orderDao.insert(new OrderLog(student.getStudentId(), "Enroll API failed"));
            }).start();
            result.setValue(Resource.error(e.getMessage(), null));
        });
    }

    private void updateStockUsedInEnroll(Student student, List<Stock> stocks, User currentUser) {
        firebaseHelper.updateStock(student, stocks, currentUser);
    }


    public MutableLiveData<Resource<Student>> getResult() {
        return result;
    }

    public void updateStock(Student student, List<Stock> stocks, User currentUser) {
        firebaseHelper.updateStock(student, stocks, currentUser);
    }

    private void placeOrder(Student student, Order order, List<Stock> stocks, User currentUser) {
        if (Student.isValidForEnroll(student)) {
            new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), "Place Order method called"))).start();
            createOrderData(student, order, currentUser);
            newOrder(order, student, stocks, currentUser);
        } else {
            // UIUtils.showToast(requireContext(), "Student data corrupted");
            if (student != null) {
                new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), "Place Order FAILURE. Student data corrupted: " + student.getStudentId()))).start();
            }
        }
    }

    private void createOrderData(Student student, Order order, User currentUser) {
        order.setCurrentLevel(student.getLevel());
        order.setOrderLevel(student.getLevel());
        order.setStudentId(student.getStudentId());
        order.setFranchiseName(currentUser.getName());
        order.setState(currentUser.getState());
        order.setDate(UIUtils.getDate());
        order.setStudentName(student.getName());
        order.setBooks(student.getItems());
        if (student.getProgram().getCourse() == Program.Course.AA) {
            if (order.getOrderLevel().getLevel() == 4) {
                order.setCertificate(Certificate.CERT_GRADUATE);
            } else if (order.getOrderLevel().getLevel() == 6) {
                order.setCertificate(Certificate.CERT_MASTER);
            } else {
                order.setCertificate("N");
            }
        } else if (student.getProgram().getCourse() == Program.Course.MA) {
            if (order.getOrderLevel().getLevel() == 3) {
                order.setCertificate(Certificate.CERT_GRADUATE);
            } else if (order.getOrderLevel().getLevel() == 5) {
                order.setCertificate(Certificate.CERT_MASTER);
            } else {
                order.setCertificate("N");
            }
        } else {
            order.setCertificate("N");
        }
        new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), "create OrderData called for " + order.getStudentName()))).start();
    }

    public void newOrder(Order order, Student student, List<Stock> stocks, User currentUser) {
        new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), order.getOrderId() + "Calling newOrder method"))).start();
        new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), order.getOrderId() + "Called order API"))).start();
        firebaseHelper.order(order, nothing -> {
            new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), order.getOrderId() + "Order API "))).start();
            result.setValue(Resource.success(student));
            if (!UIUtils.IS_DATA_IMPORT) {
                firebaseHelper.updateLastStudentId(Integer.parseInt(student.getStudentId()));
            }
            updateStockUsedInEnroll(student, stocks, currentUser);
        }, e -> {
            new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), order.getOrderId() + "Order API failed"))).start();
            result.setValue(Resource.error(e.getMessage(), null));
        });
    }

}
