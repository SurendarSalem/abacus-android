package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.CartOrder;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Program;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Session;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.CartRepository;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.repository.OrdersRepository;
import com.balaabirami.abacusandroid.room.AbacusDatabase;
import com.balaabirami.abacusandroid.room.OrderDao;
import com.balaabirami.abacusandroid.room.OrderLog;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderViewModel extends AndroidViewModel implements OrderListListener {
    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<Order>> orderResult = new MutableLiveData<>();
    private final MutableLiveData<Level> futureLevel = new MutableLiveData<>();
    private final MutableLiveData<List<Level>> levels = new MutableLiveData<>();
    private final MutableLiveData<List<String>> books = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Order>>> orderListData = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Order>>> studentOrdersData = new MutableLiveData<>();
    OrdersRepository ordersRepository;
    OrderDao orderDao;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.ORDER_REFERENCE);
        ordersRepository = OrdersRepository.getInstance();
        orderDao = Objects.requireNonNull(AbacusDatabase.Companion.getAbacusDatabase(application.getApplicationContext())).orderDao();
    }

    public MutableLiveData<Resource<Order>> getOrderResult() {
        return orderResult;
    }

    public LiveData<Level> getFutureLevels(Level level) {
        futureLevel.setValue(LevelRepository.newInstance().getFutureLevel(level));
        return futureLevel;
    }

    public Level getLevel(int pos) {
        return LevelRepository.newInstance().getLevel(pos);
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
                student.setPromotedAAtoMA(true);
            } else {
                bks.add("AA CB" + (student.getLevel().getLevel() + 1));
                bks.add("AA PB" + (student.getLevel().getLevel() + 1));
            }
        } else {
            bks.add("MA ASS PAPER L" + (student.getLevel().getLevel()));
            if (student.getLevel().getLevel() < 6) {
                bks.add("MA CB" + (student.getLevel().getLevel() + 1));
                bks.add("MA PB" + (student.getLevel().getLevel() + 1));
            } else {
                student.setCompletedCourse(true);
            }
        }
        books.setValue(bks);
        return books;
    }

    public void getAllOrders(User user) {
        firebaseHelper.getAllOrders(user, this);
    }

    public void getAllOrders(Student student) {
        firebaseHelper.getAllOrders(student, new OrderListListener() {
            @Override
            public void onOrderListLoaded(List<Order> orders) {
                studentOrdersData.setValue(Resource.success(orders));
            }

            @Override
            public void onError(String message) {
                studentOrdersData.setValue(Resource.error(message, null));
            }
        });
    }

    public MutableLiveData<Resource<List<Order>>> getOrderListData(User user) {
        List<Order> orders = ordersRepository.getOrders();
        if (orders == null || orders.isEmpty()) {
            getAllOrders(user);
        } else {
            orderListData.setValue(Resource.loading(null, null));
            orderListData.setValue(Resource.success(orders));
        }
        return orderListData;
    }

    public void order(Order order, Student student, List<Stock> stocks, User currentUser) {
        orderResult.setValue(Resource.loading(null, "Order API in progress"));
        new Thread(() -> {
            orderDao.insert(new OrderLog(order.getOrderId(), "Order - order API called"));
        }).start();
        Session.Companion.addStep("Order - order API called");
        firebaseHelper.order(order, nothing -> {
            new Thread(() -> {
                orderDao.insert(new OrderLog(order.getOrderId(), "Order - order API success"));
            }).start();
            Session.Companion.addStep("Order - order API success");
            if (student.isPromotedAAtoMA()) {
                student.setLevel(getLevel(5));
                student.setProgram(Program.getMA());
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - AA -> MA"));
                }).start();
                Session.Companion.addStep("Order - AA -> MA");
            } else {
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - AA -> MA"));
                    Session.Companion.addStep("Order - AA -> MA");
                    student.setLevel(order.getOrderLevel());
                }).start();
            }
            student.setPromotedAAtoMA(false);
            student.setLastOrderedDate(order.getDate());
            orderResult.setValue(Resource.loading(null, "Update Student API in progress"));
            new Thread(() -> {
                orderDao.insert(new OrderLog(order.getOrderId(), "Order - Update student API calling"));
            }).start();
            Session.Companion.addStep("Order - Update student API calling");
            firebaseHelper.updateStudent(student, unused -> {
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - Update student API success"));
                }).start();
                Session.Companion.addStep("Order - Update student API success");
                updateStockUsedInOrder(stocks, order, currentUser, student);
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - orderResult.setValue called"));
                }).start();
                Session.Companion.addStep("Order - orderResult.setValue called");
                orderResult.setValue(Resource.success(order));
            }, e -> {
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - Update student API failure"));
                }).start();
                Session.Companion.addStep("Order - Update student API failure");
                if (student.getLevel().getLevel() >= 6) {
                    student.setLevel(getLevel(6));
                }
                student.setCompletedCourse(false);
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - orderResult.setValue called"));
                }).start();
                Session.Companion.addStep("Order - orderResult.setValue called");
                orderResult.setValue(Resource.error(e.getMessage(), null));
            });
        }, e -> {
            new Thread(() -> {
                orderDao.insert(new OrderLog(order.getOrderId(), "Order - Order API failed"));
            }).start();
            Session.Companion.addStep("Order - Order API failed");
            if (student.getLevel().getLevel() >= 6) {
                student.setLevel(getLevel(6));
            }
            student.setCompletedCourse(false);
            new Thread(() -> {
                orderDao.insert(new OrderLog(order.getOrderId(), "Order - orderResult.setValue called"));
            }).start();
            Session.Companion.addStep("Order - orderResult.setValue called");
            orderResult.setValue(Resource.error(e.getMessage(), null));
        });
    }

    public void newOrder(Order order, Student
            student, List<Stock> stocks, User currentUser) {
        new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), order.getOrderId() + "Calling newOrder method"))).start();
        new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), order.getOrderId() + "Called order API"))).start();
        orderResult.setValue(Resource.loading(null, null));
        firebaseHelper.order(order, nothing -> {
            new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), order.getOrderId() + "Order API "))).start();
            orderResult.setValue(Resource.success(order));
        }, e -> {
            new Thread(() -> orderDao.insert(new OrderLog(order.getOrderId(), order.getOrderId() + "Order API failed"))).start();
            orderResult.setValue(Resource.error(e.getMessage(), null));
        });
    }

    private void updateStockUsedInOrder
            (List<Stock> stocks, Order order, User
                    currentUser, Student student) {
        firebaseHelper.updateStock(order.getBooks(), stocks, currentUser, student);
    }

    @Override
    public void onOrderListLoaded
            (List<Order> orders) {
        ordersRepository.setOrders(orders);
        orderListData.setValue(Resource.success(orders));
    }

    @Override
    public void onError(String message) {

    }

    public MutableLiveData<Resource<List<Order>>> getStudentOrdersData
            () {
        return studentOrdersData;
    }

    public boolean addToCart(CartOrder cartOrder) {
        return CartRepository.getInstance().addToCart(cartOrder);
    }
}
