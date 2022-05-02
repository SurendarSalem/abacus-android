package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Program;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.repository.OrdersRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends AndroidViewModel implements OrderListListener {
    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<Order>> result = new MutableLiveData<>();
    private final MutableLiveData<Level> futureLevel = new MutableLiveData<>();
    private final MutableLiveData<List<Level>> levels = new MutableLiveData<>();
    private final MutableLiveData<List<String>> books = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Order>>> orderListData = new MutableLiveData<>();
    OrdersRepository ordersRepository;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.ORDER_REFERENCE);
        ordersRepository = OrdersRepository.getInstance();
    }

    public MutableLiveData<Resource<Order>> getResult() {
        return result;
    }

    public LiveData<Level> getFutureLevels(Level level) {
        futureLevel.setValue(LevelRepository.newInstance().getFutureLevel(level));
        return futureLevel;
    }

    public LiveData<List<Level>> getLevels() {
        if (levels.getValue() == null || levels.getValue().isEmpty()) {
            levels.setValue(LevelRepository.newInstance().getLevels());
        }
        return levels;
    }

    public LiveData<List<String>> getBooks(Student student) {
        List<String> bks = new ArrayList<>();
        Program AA = new Program();
        AA.setCourse(Program.Course.AA);
        if (student.getProgram().equals(AA)) {
            bks.add("AA ASS PAPER L" + (student.getLevel().getLevel()));
            if (student.getLevel().getLevel() >= 6) {
                bks.add("MA CB5");
                bks.add("MA PB5");
            } else {
                bks.add("AA CB" + (student.getLevel().getLevel() + 1));
                bks.add("AA PB" + (student.getLevel().getLevel() + 1));
            }
        } else {
            bks.add("MA ASS PAPER L" + (student.getLevel().getLevel()));
            bks.add("MA CB" + (student.getLevel().getLevel() + 1));
            bks.add("MA PB" + (student.getLevel().getLevel() + 1));
        }
        books.setValue(bks);
        return books;
    }

    public void getAllOrders(User user) {
        firebaseHelper.getAllOrders(user, this);
    }

    public MutableLiveData<Resource<List<Order>>> getOrderListData(User user) {
        List<Order> orders = ordersRepository.getOrders();
        if (orders == null || orders.isEmpty()) {
            getAllOrders(user);
        } else {
            orderListData.setValue(Resource.loading(null));
            orderListData.setValue(Resource.success(orders));
        }
        return orderListData;
    }

    public void order(Order order, Student student, List<Stock> stocks, User currentUser) {
        result.setValue(Resource.loading(null));
        firebaseHelper.order(order, nothing -> {
            student.setLevel(order.getOrderLevel());
            student.setLastOrderedDate(order.getDate());
            firebaseHelper.updateStudent(student, null, null);
            updateStockUsedInOrder(stocks, order, currentUser, student);
            result.setValue(Resource.success(order));
        }, e -> {
            result.setValue(Resource.error(e.getMessage(), null));
        });
    }

    public void newOrder(Order order, Student student, List<Stock> stocks, User currentUser) {
        result.setValue(Resource.loading(null));
        firebaseHelper.order(order, nothing -> {
            result.setValue(Resource.success(order));
        }, e -> {
            result.setValue(Resource.error(e.getMessage(), null));
        });
    }

    private void updateStockUsedInOrder(List<Stock> stocks, Order order, User currentUser, Student student) {
        firebaseHelper.updateStock(order.getBooks(), stocks, currentUser, student);
    }

    @Override
    public void onOrderListLoaded(List<Order> orders) {
        ordersRepository.setOrders(orders);
        orderListData.setValue(Resource.success(orders));
    }

    @Override
    public void onError(String message) {

    }
}
