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
import com.balaabirami.abacusandroid.model.StockAdjustment;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.utils.DataLoadListener;
import com.balaabirami.abacusandroid.utils.StateHelper;
import com.balaabirami.abacusandroid.utils.UIUtils;

import java.util.List;
import java.util.Objects;

public class StockAdjustViewModel extends AndroidViewModel {

    private final MutableLiveData<Resource<StockAdjustment>> result = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<StockAdjustment>>> stockAdjustListData = new MutableLiveData<>();
    private final FirebaseHelper firebaseHelper;
    private int updateCount = 0;

    public StockAdjustViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.STOCK_ADJUSTMENT_REFERENCE);
    }

    public void createStockAdjust(StockAdjustment stockAdjustment, List<Stock> stocks) {
        result.setValue(Resource.loading(null, null));
        firebaseHelper.createStockStock(stockAdjustment, nothing -> {
            updateCount = 0;
            updateStock(stockAdjustment, stocks);
        }, e -> {
            result.setValue(Resource.error(e.getMessage(), null));
        });
    }

    private void updateStock(StockAdjustment stockAdjustment, List<Stock> stocks) {
        if (stocks != null) {
            for (StockAdjustment.ItemDetail itemDetail : Objects.requireNonNull(stockAdjustment.getItems())) {
                Stock stockFromAdjust = itemDetail.getName();
                if (stockFromAdjust != null && stocks.contains(stockFromAdjust)) {
                    Stock stockFromRepo = stocks.get(stocks.indexOf(stockFromAdjust));
                    stockFromRepo.setQuantity(stockFromRepo.getQuantity() - itemDetail.getQty());
                    firebaseHelper.updateStock(stockFromRepo, stock -> {
                        updateCount++;
                        if (updateCount >= stockAdjustment.getItems().size()) {
                            updateCount = 0;
                            result.setValue(Resource.success(stockAdjustment));
                        }
                    }, error -> {
                        updateCount = 0;
                        result.setValue(Resource.error("Stock update failed", null));
                    });
                }
            }
        }

    }

    public void getAllStockAdjustments() {
        stockAdjustListData.setValue(Resource.loading(null, null));
        firebaseHelper.getAllStockAdjustments((DataLoadListener<StockAdjustment>) data -> {
            stockAdjustListData.setValue(Resource.success(data));
        });
    }

    public MutableLiveData<Resource<StockAdjustment>> getResult() {
        return result;
    }

    public MutableLiveData<Resource<List<StockAdjustment>>> getStockAdjustLisaData() {
        return stockAdjustListData;
    }
}

