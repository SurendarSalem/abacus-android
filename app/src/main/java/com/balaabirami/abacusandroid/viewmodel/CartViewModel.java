package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Cart;
import com.balaabirami.abacusandroid.model.CartOrder;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.repository.OrdersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CartViewModel extends AndroidViewModel implements OrderListListener {
    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<List<CartOrder>>> orderResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Order>>> orderListData = new MutableLiveData<>();
    OrdersRepository ordersRepository;

    public CartViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.ORDER_REFERENCE);
        ordersRepository = OrdersRepository.getInstance();
    }

    public Level getLevel(int pos) {
        return LevelRepository.newInstance().getLevel(pos);
    }

    public void orderFromCart(List<CartOrder> cartOrders) {
        orderResult.setValue(Resource.loading(null));
        List<CartOrder> failedOrders = new ArrayList<>();
        for (CartOrder cartOrder : cartOrders) {
            firebaseHelper.order(cartOrder.getOrder(), nothing -> {
                cartOrder.getStudent().setLevel(cartOrder.getOrder().getOrderLevel());
                cartOrder.getStudent().setLastOrderedDate(cartOrder.getOrder().getDate());
                firebaseHelper.updateStudent(cartOrder.getStudent(), unused -> {
                    updateStockUsedInOrder(cartOrder.getStocks(), cartOrder.getOrder(), cartOrder.getCurrentUser(), cartOrder.getStudent());
                    int index = cartOrders.indexOf(cartOrder);
                    if (index >= cartOrders.size() - 1) {
                        orderResult.setValue(Resource.success(failedOrders));
                    }
                }, e -> {
                    failedOrders.add(cartOrder);
                    if (cartOrders.indexOf(cartOrder) >= cartOrders.size() - 1) {
                        orderResult.setValue(Resource.error(e.getMessage(), failedOrders));
                    }
                });
            }, e -> {
                failedOrders.add(cartOrder);
                if (cartOrders.indexOf(cartOrder) >= cartOrders.size() - 1) {
                    orderResult.setValue(Resource.error(e.getMessage(), failedOrders));
                }
            });
        }
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
        // TODO document why this method is empty
    }

    public MutableLiveData<Resource<List<CartOrder>>> getOrderResult() {
        return orderResult;
    }
}
