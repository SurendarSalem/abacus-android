package com.balaabirami.abacusandroid.ui.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.viewmodel.StockListListener;

import java.util.List;

public class StockListViewModel extends AndroidViewModel implements StockListListener {

    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<Stock>> stockListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Stock>> stockUpdateLiveData = new MutableLiveData<>();


    public StockListViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.STOCK_REFERENCE);
    }

    public void getAllStocks() {
        stockListLiveData.setValue(Resource.loading(null));
        firebaseHelper.getAllStocks(this);
    }

    public void updateStock(Stock stock) {
        stockListLiveData.setValue(Resource.loading(null));
        firebaseHelper.updateStock(stock, this);
    }

    public void addTransaction(StockTransaction stockTransaction) {
        firebaseHelper.init(FirebaseHelper.TRANSACTIONS_REFERENCE);
        firebaseHelper.addTransaction(stockTransaction, null, null);
    }


    public MutableLiveData<Resource<Stock>> getStockListLiveData() {
        return stockListLiveData;
    }

    public MutableLiveData<Resource<Stock>> getStockUpdateLiveData() {
        return stockUpdateLiveData;
    }

    @Override
    public void onStockListLoaded(Stock stock) {
        stockListLiveData.setValue(Resource.success(stock));
    }

    @Override
    public void onStockListLoaded(List<Stock> stocks) {

    }

    @Override
    public void onStockUpdated(Stock stock) {
        stockUpdateLiveData.setValue(Resource.success(stock, "Stock updated!"));
    }

    public void clearListener() {
        firebaseHelper.clearListener();
    }
}