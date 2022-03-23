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
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.repository.OrdersRepository;
import com.balaabirami.abacusandroid.repository.StudentsRepository;

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

    public LiveData<List<String>> getBooks(Program program) {
        List<String> bks = new ArrayList<>();
        Program AA = new Program();
        AA.setCourse(Program.Course.AA);
        if (program.equals(AA)) {
            bks.add("Level AA Assessment paper");
            bks.add("CB AA");
            bks.add("PB AA");
        } else {
            bks.add("Level MA Assessment paper");
            bks.add("CB MA");
            bks.add("PB MA");
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

    public void order(Order order) {
        result.setValue(Resource.loading(null));
        firebaseHelper.order(order, nothing -> {
            result.setValue(Resource.success(order));
        }, e -> {
            result.setValue(Resource.error(e.getMessage(), null));
        });
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
